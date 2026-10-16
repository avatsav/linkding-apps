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

  /** Returns after durable local storage; the foreground uploader owns all network work. */
  suspend fun create(bookmark: SaveBookmark): Result<SaveResult, BookmarkError>

  /** False if the item is being uploaded, or was uploaded/discarded while its editor was open. */
  suspend fun edit(id: Long, bookmark: SaveBookmark): Boolean

  /** Does not wait for network requests. An item already being POSTed cannot be discarded. */
  suspend fun discard(id: Long)

  /**
   * Requests a retry without network work. The next foreground uploader also checks requested items
   * requiring attention; automatic uploads skip them. Requests wait in memory while no uploader is
   * active, and are consumed before POST so uncertain uploads need a fresh retry.
   */
  suspend fun retry(id: Long? = null)

  /** Runs until cancelled. The authenticated foreground lifecycle owns this call. */
  suspend fun runUploader()
}
