package dev.avatsav.linkding.bookmarks.impl

import com.github.michaelbull.result.fold
import dev.avatsav.linkding.api.LinkdingBookmarksApi
import dev.avatsav.linkding.api.models.LinkdingError
import dev.avatsav.linkding.bookmarks.impl.PendingBookmarksCoordinator.Request
import dev.avatsav.linkding.bookmarks.impl.mappers.BookmarkMapper
import dev.avatsav.linkding.data.db.daos.BookmarksDao
import dev.avatsav.linkding.data.db.daos.PendingBookmarksDao
import dev.avatsav.linkding.data.model.ApiConfig
import dev.avatsav.linkding.data.model.PendingBookmark
import dev.avatsav.linkding.data.model.PendingBookmark.State
import dev.avatsav.linkding.di.scope.UserScope
import dev.avatsav.linkding.internet.LocalNetworkAccess
import dev.avatsav.linkding.prefs.AppPreferences
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/** Upload policy only. The authenticated foreground caller owns execution and cancellation. */
@Inject
@SingleIn(UserScope::class)
class PendingBookmarksUploader(
  private val dao: PendingBookmarksDao,
  private val bookmarksDao: BookmarksDao,
  private val api: LinkdingBookmarksApi,
  private val mapper: BookmarkMapper,
  private val preferences: AppPreferences,
  private val config: ApiConfig,
  private val localNetworkAccess: LocalNetworkAccess,
  private val coordinator: PendingBookmarksCoordinator,
) {
  private val accountId = preferences.getAccountId()

  suspend fun run() {
    val account = coordinator.account(accountId)
    // Hold ownership for the entire foreground lifetime, including recovery. Other activities
    // wait here and take over only after this owner has finished cancellation cleanup.
    account.uploaderMutex.withLock {
      coordinator.locally(accountId) {
        for (item in dao.getAll(accountId).filter { it.state == State.Uploading }) {
          dao.update(accountId, item.copy(state = State.Uncertain))
        }
      }
      val timer = flow {
        while (true) {
          emit(Unit)
          delay(60.seconds)
        }
      }
      val accessGranted =
        localNetworkAccess
          .observe(config.hostUrl)
          .filter { it != LocalNetworkAccess.State.Required }
          .map { Unit }
      merge(timer, coordinator.connectivityWakeups, account.wakeups.receiveAsFlow(), accessGranted)
        .collect { drain() }
    }
  }

  private suspend fun drain() {
    val ids =
      coordinator.locally(accountId) {
        dao.getAll(accountId).map { it.id }.also { retryRequests.keys.retainAll(it.toSet()) }
      }
    if (
      ids.isEmpty() || localNetworkAccess.check(config.hostUrl) == LocalNetworkAccess.State.Required
    )
      return
    for (pendingId in ids) {
      val request = coordinator.locally(accountId) { retryRequests[pendingId] }
      upload(pendingId, request)
      // Cancellation leaves a preflight request intact. Never erase newer intent queued while
      // this attempt was looking up a row or publishing its result.
      coordinator.locally(accountId) {
        if (retryRequests[pendingId] == request) retryRequests.remove(pendingId)
      }
    }
  }

  private val isCurrentAccount: Boolean
    get() = preferences.getApiConfig() == config && preferences.getAccountId() == accountId

  private suspend fun get(id: Long): PendingBookmark? = dao.getAll(accountId).find { it.id == id }

  private suspend fun upload(id: Long, request: Request?) {
    val item =
      coordinator.locally(accountId) {
        get(id)?.takeIf {
          isCurrentAccount &&
            when (it.state) {
              State.Pending -> true
              State.Failed,
              State.Uncertain -> request != null && !request.overwrite
              State.AlreadyExists -> request?.overwrite == true
              State.Uploading -> false
            }
        }
      } ?: return
    // Preflight is outside the local lock: editing and discarding remain available.
    api
      .checkUrl(item.bookmark.url)
      .fold(
        success = { check ->
          val exists = check.bookmark != null
          if (exists && request?.overwrite != true) {
            updateIfUnchanged(item, State.AlreadyExists)
          } else {
            send(item, cacheOnSuccess = !exists)
          }
        },
        failure = { error ->
          if (error !is LinkdingError.Connectivity)
            updateIfUnchanged(item, State.Failed, error.message)
        },
      )
  }

  private suspend fun claim(checked: PendingBookmark): PendingBookmark? =
    coordinator.locally(accountId) {
      val latest = get(checked.id) ?: return@locally null
      if (!isCurrentAccount || latest.state == State.Uploading) return@locally null
      // A changed URL needs its own preflight. An overwrite only applies to the confirmed draft.
      if (latest.bookmark.url != checked.bookmark.url) return@locally null
      if (checked.state == State.AlreadyExists && latest != checked) return@locally null

      // A request recorded after a visible failure has its own generation.
      retryRequests.remove(latest.id)
      latest.copy(state = State.Uploading, lastError = null).also { dao.update(accountId, it) }
    }

  private suspend fun send(checked: PendingBookmark, cacheOnSuccess: Boolean) {
    if (localNetworkAccess.check(config.hostUrl) == LocalNetworkAccess.State.Required) return
    val item = claim(checked) ?: return
    try {
      api
        .saveBookmark(mapper.map(item.bookmark))
        .fold(
          success = { response ->
            coordinator.locally(accountId) {
              if (get(item.id) == item) {
                // The feed handles existing rows; adding one here may skew its paging offset.
                if (cacheOnSuccess && isCurrentAccount) bookmarksDao.upsert(mapper.map(response))
                dao.delete(accountId, item.id)
              }
            }
          },
          failure = { error ->
            val uncertain =
              error is LinkdingError.Connectivity ||
                (error is LinkdingError.Other && error.requestMayHaveSucceeded)
            if (uncertain) updateIfUnchanged(item, State.Uncertain)
            else updateIfUnchanged(item, State.Failed, error.message)
          },
        )
    } catch (cancelled: CancellationException) {
      withContext(NonCancellable) { updateIfUnchanged(item, State.Uncertain) }
      throw cancelled
    }
  }

  private suspend fun updateIfUnchanged(
    item: PendingBookmark,
    state: State,
    error: String? = null,
  ) =
    coordinator.locally(accountId) {
      // A slow preflight failure must not overwrite fields saved or discarded in the meantime.
      if (get(item.id) == item) dao.update(accountId, item.copy(state = state, lastError = error))
    }
}
