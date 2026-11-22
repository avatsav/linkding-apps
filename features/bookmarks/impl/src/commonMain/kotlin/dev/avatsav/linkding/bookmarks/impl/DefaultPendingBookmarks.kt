package dev.avatsav.linkding.bookmarks.impl

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import dev.avatsav.linkding.bookmarks.api.PendingBookmarks
import dev.avatsav.linkding.data.db.daos.PendingBookmarksDao
import dev.avatsav.linkding.data.model.ApiConfig
import dev.avatsav.linkding.data.model.BookmarkError
import dev.avatsav.linkding.data.model.PendingBookmark
import dev.avatsav.linkding.data.model.PendingBookmark.State
import dev.avatsav.linkding.data.model.SaveBookmark
import dev.avatsav.linkding.data.model.SaveResult
import dev.avatsav.linkding.di.scope.UserScope
import dev.avatsav.linkding.prefs.AppPreferences
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlin.time.Clock
import kotlinx.coroutines.flow.Flow

@Inject
@SingleIn(UserScope::class)
@ContributesBinding(UserScope::class)
class DefaultPendingBookmarks(
  private val dao: PendingBookmarksDao,
  private val preferences: AppPreferences,
  private val config: ApiConfig,
  private val coordinator: PendingBookmarksCoordinator,
  private val uploader: PendingBookmarksUploader,
) : PendingBookmarks {
  private val accountId = preferences.getAccountId()

  override fun observe(): Flow<List<PendingBookmark>> = dao.observeAll(accountId)

  override fun observe(id: Long): Flow<PendingBookmark?> = dao.observeById(accountId, id)

  override suspend fun get(id: Long): PendingBookmark? = dao.getById(accountId, id)

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
      // Do not change an in-flight payload or a draft awaiting a conflict decision.
      if (!item.canEdit) return@locally false
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
        retryRequests.remove(id)
      }
      Unit
    }

  override suspend fun retry(id: Long?) =
    coordinator.locally(accountId) {
      if (!isCurrentAccount) return@locally
      // Snapshot the requested rows, not future creations or an already active POST.
      dao
        .getAll(accountId)
        .filter {
          (id == null || it.id == id) &&
            it.state != State.Uploading &&
            it.state != State.AlreadyExists
        }
        .forEach { request(it.id) }
    }

  override suspend fun overwrite(id: Long) =
    coordinator.locally(accountId) {
      if (get(id)?.state == State.AlreadyExists && isCurrentAccount) request(id, overwrite = true)
    }

  override suspend fun runUploader() = uploader.run()

  private val isCurrentAccount: Boolean
    get() = preferences.getApiConfig() == config && preferences.getAccountId() == accountId
}
