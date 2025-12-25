package dev.avatsav.linkding.data.model

data class UpdateBookmark(
  val id: Long,
  val title: String? = null,
  val description: String? = null,
  val notes: String? = null,
  val tags: Set<String>? = null,
)
