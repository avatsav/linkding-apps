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
import dev.avatsav.linkding.internet.ConnectivityObserver
import dev.avatsav.linkding.prefs.AppPreferences
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.sync.Mutex
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
    private val connectivity: ConnectivityObserver,
) : PendingBookmarks {
  private val accountId = preferences.getAccountId()
  private val mutex = Mutex()
  private val wakeups = Channel<Unit>(Channel.CONFLATED)

  override fun observe(): Flow<List<PendingBookmark>> = dao.observeAll(accountId)

  override suspend fun get(id: Long): PendingBookmark? = dao.getAll(accountId).find { it.id == id }

  override suspend fun create(bookmark: SaveBookmark): Result<SaveResult, BookmarkError> =
      mutex.withLock {
        if (!isCurrentAccount()) return@withLock Err(BookmarkError("Account changed"))
        val draft = PendingBookmark(bookmark = bookmark, createdAt = Clock.System.now())
        val id = dao.insert(accountId, draft)
        val item = draft.copy(id = id)
        if (!connectivity.observeIsOnline.value) {
          // Do not make an offline save wait for a network timeout. The foreground uploader
          // still probes the configured host, including hosts reachable only on a local LAN.
          wakeups.trySend(Unit)
          return@withLock Ok(SaveResult.Queued(id))
        }
        // Persist before sending: process death must not lose a submitted creation.
        val result = upload(item)
        if (result.isErr && get(id)?.state == State.Failed) {
          // A definite rejection of a new save belongs in the open form, not in the queue.
          dao.delete(accountId, id)
        }
        result
      }

  override suspend fun edit(id: Long, bookmark: SaveBookmark): Boolean = mutex.withLock {
    if (!isCurrentAccount()) return@withLock false
    val item = get(id) ?: return@withLock false
    // An ambiguous request may already exist remotely; retain that warning after editing.
    val uncertain = item.state == State.Uncertain || item.state == State.Uploading
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

  override suspend fun discard(id: Long) = mutex.withLock { dao.delete(accountId, id) }

  override suspend fun retry(id: Long?) = mutex.withLock {
    if (!isCurrentAccount()) return@withLock
    for (item in dao.getAll(accountId)) {
      if (id == null || item.id == id) upload(item)
    }
  }

  override suspend fun runUploader() {
    // A persisted Uploading state means the previous attempt ended without acknowledgement.
    mutex.withLock {
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
    merge(timer, connectivity.observeIsOnline.filter { it }.map { Unit }, wakeups.receiveAsFlow())
        .collect {
          mutex.withLock {
            if (isCurrentAccount()) {
              for (item in dao.getAll(accountId).filter { it.state == State.Pending }) upload(item)
            }
          }
        }
  }

  private fun isCurrentAccount() =
      preferences.getApiConfig() == config && preferences.getAccountId() == accountId

  @IgnorableReturnValue
  private suspend fun upload(item: PendingBookmark): Result<SaveResult, BookmarkError> {
    if (!isCurrentAccount()) return Ok(SaveResult.Queued(item.id))
    // Check before POST. Never create over an existing URL or assume idempotency support.
    return api.checkUrl(item.bookmark.url)
        .fold(
            success = { check ->
              if (check.bookmark != null) {
                fail(
                    item,
                    "This URL is already on the server. Review it before discarding this pending bookmark.",
                )
              } else send(item)
            },
            failure = { error ->
              if (error is LinkdingError.Connectivity) Ok(SaveResult.Queued(item.id))
              else fail(item, error.message)
            },
        )
  }

  private suspend fun send(item: PendingBookmark): Result<SaveResult, BookmarkError> {
    if (!isCurrentAccount()) return Ok(SaveResult.Queued(item.id))
    // Persist before POST so abrupt process death is recoverable too.
    dao.update(accountId, item.copy(state = State.Uploading, lastError = null))
    return try {
      api.saveBookmark(mapper.map(item.bookmark))
          .fold(
              success = { response ->
                val bookmark = mapper.map(response)
                bookmarksDao.upsert(bookmark)
                dao.delete(accountId, item.id)
                Ok(SaveResult.Synced(bookmark))
              },
              failure = { error ->
                val uncertain =
                    error is LinkdingError.Connectivity ||
                        (error is LinkdingError.Other && error.requestMayHaveSucceeded)
                if (uncertain) {
                  dao.update(accountId, item.copy(state = State.Uncertain))
                  Ok(SaveResult.Queued(item.id))
                } else fail(item, error.message)
              },
          )
    } catch (cancelled: CancellationException) {
      withContext(NonCancellable) {
        // Closing the editor must not leave an uneditable Uploading row until app restart.
        if (get(item.id) != null) dao.update(accountId, item.copy(state = State.Uncertain))
      }
      throw cancelled
    }
  }

  private suspend fun fail(
      item: PendingBookmark,
      message: String,
  ): Result<SaveResult, BookmarkError> {
    dao.update(accountId, item.copy(state = State.Failed, lastError = message))
    return Err(BookmarkError(message))
  }
}
