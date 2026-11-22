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

  fun observe(id: Long): Flow<PendingBookmark?>

  suspend fun get(id: Long): PendingBookmark?

  /** Returns after durable local storage; the foreground uploader owns all network work. */
  suspend fun create(bookmark: SaveBookmark): Result<SaveResult, BookmarkError>

  /** False if the item was removed, is uploading, or awaits an overwrite/discard decision. */
  suspend fun edit(id: Long, bookmark: SaveBookmark): Boolean

  /** Does not wait for network requests. An item already being uploaded cannot be discarded. */
  suspend fun discard(id: Long)

  /**
   * Requests a retry without network work. The next foreground uploader also checks requested items
   * requiring attention, except known URL matches that need an explicit overwrite. Requests wait in
   * memory and are consumed before upload so uncertain uploads need a fresh decision.
   */
  suspend fun retry(id: Long? = null)

  /** Authorizes one foreground attempt to save a draft whose URL already exists on the server. */
  suspend fun overwrite(id: Long)

  /** Runs until cancelled. The authenticated foreground lifecycle owns this call. */
  suspend fun runUploader()
}
