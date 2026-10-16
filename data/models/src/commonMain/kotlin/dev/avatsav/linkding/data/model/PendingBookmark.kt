package dev.avatsav.linkding.data.model

import kotlin.time.Instant

/** An unsent creation, never a local version of an existing server bookmark. */
data class PendingBookmark(
  val id: Long = 0,
  val bookmark: SaveBookmark,
  val createdAt: Instant,
  val state: State = State.Pending,
  val lastError: String? = null,
) {
  enum class State {
    Pending,
    Uploading,
    Failed,
    Uncertain,
  }
}

sealed interface SaveResult {
  data class Synced(val bookmark: Bookmark) : SaveResult

  data class Queued(val id: Long) : SaveResult
}
