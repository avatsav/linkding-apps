package dev.avatsav.linkding.bookmarks.impl

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.fold
import dev.avatsav.linkding.api.LinkdingBookmarksApi
import dev.avatsav.linkding.api.models.LinkdingError
import dev.avatsav.linkding.bookmarks.api.PendingBookmarks
import dev.avatsav.linkding.bookmarks.impl.mappers.BookmarkMapper
import dev.avatsav.linkding.data.db.daos.BookmarksDao
import dev.avatsav.linkding.data.db.daos.PendingBookmarksDao
import dev.avatsav.linkding.data.model.ApiConfig
import dev.avatsav.linkding.data.model.BookmarkError
import dev.avatsav.linkding.data.model.PendingBookmark
import dev.avatsav.linkding.data.model.PendingBookmark.State
import dev.avatsav.linkding.data.model.SaveBookmark
import dev.avatsav.linkding.data.model.SaveResult
import dev.avatsav.linkding.di.scope.UserScope
import dev.avatsav.linkding.internet.LocalNetworkAccess
import dev.avatsav.linkding.prefs.AppPreferences
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

@Inject
@SingleIn(UserScope::class)
@ContributesBinding(UserScope::class)
class DefaultPendingBookmarks(
  private val dao: PendingBookmarksDao,
  private val bookmarksDao: BookmarksDao,
  private val api: LinkdingBookmarksApi,
  private val mapper: BookmarkMapper,
  private val preferences: AppPreferences,
  private val config: ApiConfig,
  private val localNetworkAccess: LocalNetworkAccess,
  private val coordinator: PendingBookmarksCoordinator,
) : PendingBookmarks {
  private val accountId = preferences.getAccountId()

  override fun observe(): Flow<List<PendingBookmark>> = dao.observeAll(accountId)

  override suspend fun get(id: Long): PendingBookmark? = dao.getAll(accountId).find { it.id == id }

  override suspend fun create(bookmark: SaveBookmark): Result<SaveResult, BookmarkError> =
    coordinator.locally(accountId) {
      if (!isCurrentAccount) return@locally Err(BookmarkError("Account changed"))
      val draft = PendingBookmark(bookmark = bookmark, createdAt = Clock.System.now())
      val id = dao.insert(accountId, draft)
      wakeups.trySend(Unit)
      Ok(SaveResult(pendingId = id))
    }

  override suspend fun edit(id: Long, bookmark: SaveBookmark): Boolean =
    coordinator.locally(accountId) {
      if (!isCurrentAccount) return@locally false
      val item = get(id) ?: return@locally false
      // Once POST starts its payload cannot be changed. Reject promptly instead of waiting.
      if (item.state == State.Uploading) return@locally false
      val uncertain = item.state == State.Uncertain
      dao.update(
        accountId,
        item.copy(
          bookmark = bookmark,
          state = if (uncertain) State.Uncertain else State.Pending,
          lastError = if (uncertain) item.lastError else null,
        ),
      )
      wakeups.trySend(Unit)
      true
    }

  override suspend fun discard(id: Long) =
    coordinator.locally(accountId) {
      if (get(id)?.state != State.Uploading) {
        dao.delete(accountId, id)
        retryIds.remove(id)
      }
      Unit
    }

  override suspend fun retry(id: Long?) =
    coordinator.locally(accountId) {
      if (!isCurrentAccount) return@locally
      // Snapshot the requested rows, not future creations or an already active POST.
      retryIds +=
        dao
          .getAll(accountId)
          .filter { (id == null || it.id == id) && it.state != State.Uploading }
          .map { it.id }
      wakeups.trySend(Unit)
      Unit
    }

  override suspend fun runUploader() {
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
      merge(
          timer,
          coordinator.connectivityWakeups,
          account.wakeups.receiveAsFlow(),
          accessGranted,
        )
        .collect { drain() }
    }
  }

  private suspend fun drain() {
    val ids =
      coordinator.locally(accountId) {
        dao.getAll(accountId).map { it.id }.also { retryIds.retainAll(it.toSet()) }
      }
    if (
      ids.isEmpty() || localNetworkAccess.check(config.hostUrl) == LocalNetworkAccess.State.Required
    )
      return
    for (pendingId in ids) {
      val explicitRetry = coordinator.locally(accountId) { pendingId in retryIds }
      upload(pendingId, onlyPending = !explicitRetry)
      // A cancelled preflight keeps its request for the next foreground owner. POST claims
      // consume it earlier, so an ambiguous POST can never become an automatic retry.
      coordinator.locally(accountId) { retryIds -= pendingId }
    }
  }

  private val isCurrentAccount: Boolean
    get() = preferences.getApiConfig() == config && preferences.getAccountId() == accountId

  private suspend fun upload(id: Long, onlyPending: Boolean) {
    val item =
      coordinator.locally(accountId) {
        get(id)?.takeIf {
          isCurrentAccount &&
            it.state != State.Uploading &&
            (!onlyPending || it.state == State.Pending)
        }
      } ?: return
    // Preflight is outside the local lock: editing and discarding remain available.
    api
      .checkUrl(item.bookmark.url)
      .fold(
        success = { check ->
          if (check.bookmark != null) {
            updateIfUnchanged(
              item,
              State.Failed,
              "This URL is already on the server. Review it before discarding this pending bookmark.",
            )
          } else send(item)
        },
        failure = { error ->
          if (error !is LinkdingError.Connectivity)
            updateIfUnchanged(item, State.Failed, error.message)
        },
      )
  }

  private suspend fun send(checked: PendingBookmark) {
    if (localNetworkAccess.check(config.hostUrl) == LocalNetworkAccess.State.Required) return
    val item =
      coordinator.locally(accountId) {
        val latest = get(checked.id)
        if (
          !isCurrentAccount ||
            latest == null ||
            latest.state == State.Uploading ||
            latest.bookmark.url != checked.bookmark.url
        ) {
          // A changed URL needs its own preflight; a discarded row must not be recreated.
          null
        } else {
          // Claim the latest fields, not the snapshot from before the URL check.
          retryIds.remove(latest.id)
          latest.copy(state = State.Uploading, lastError = null).also { dao.update(accountId, it) }
        }
      } ?: return
    try {
      api
        .saveBookmark(mapper.map(item.bookmark))
        .fold(
          success = { response ->
            coordinator.locally(accountId) {
              if (get(item.id) == item) {
                if (isCurrentAccount) bookmarksDao.upsert(mapper.map(response))
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
