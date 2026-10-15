package dev.avatsav.linkding.bookmarks.api

import com.github.michaelbull.result.Result
import dev.avatsav.linkding.data.model.BookmarkError
import dev.avatsav.linkding.data.model.PendingBookmark
import dev.avatsav.linkding.data.model.SaveBookmark
import dev.avatsav.linkding.data.model.SaveResult
import kotlinx.coroutines.flow.Flow

/** Durable creations only. Existing server bookmarks never enter this queue. */
interface PendingBookmarks {
  fun observe(): Flow<List<PendingBookmark>>

  suspend fun get(id: Long): PendingBookmark?

  suspend fun create(bookmark: SaveBookmark): Result<SaveResult, BookmarkError>

  /** False if the item was uploaded or discarded while its editor was open. */
  suspend fun edit(id: Long, bookmark: SaveBookmark): Boolean

  suspend fun discard(id: Long)

  /** Explicit retries also check items requiring attention; automatic uploads skip them. */
  suspend fun retry(id: Long? = null)

  /** Runs until cancelled. The authenticated foreground lifecycle owns this call. */
  suspend fun runUploader()
}
