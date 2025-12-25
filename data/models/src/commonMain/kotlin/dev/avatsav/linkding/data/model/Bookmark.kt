package dev.avatsav.linkding.data.model

import kotlin.time.Instant

data class Bookmark(
  val localId: Long = 0,
  val id: Long,
  val url: String,
  val urlHost: String,
  val title: String,
  val description: String,
  val notes: String = "",
  val webArchiveSnapshotUrl: String? = null,
  val faviconUrl: String? = null,
  val previewImageUrl: String? = null,
  val archived: Boolean = false,
  val unread: Boolean = false,
  val tags: Set<String> = emptySet(),
  val added: Instant? = null,
  val modified: Instant? = null,
)
