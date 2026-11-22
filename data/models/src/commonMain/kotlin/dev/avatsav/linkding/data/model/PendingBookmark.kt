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
  val canEdit: Boolean
    get() = state != State.Uploading && state != State.AlreadyExists

  enum class State {
    Pending,
    Uploading,
    Failed,
    Uncertain,
    AlreadyExists,
  }
}

/** A creation acknowledged after it has been durably queued, not uploaded. */
data class SaveResult(val pendingId: Long)
