package dev.avatsav.linkding.data.model

data class CheckUrlResult(
  val existingBookmark: Bookmark?,
  val url: String,
  val title: String?,
  val description: String?,
) {
  val alreadyBookmarked: Boolean
    get() = existingBookmark != null
}
