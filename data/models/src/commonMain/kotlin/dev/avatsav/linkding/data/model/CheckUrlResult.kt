package dev.avatsav.linkding.data.model

data class CheckUrlResult(
  val alreadyBookmarked: Boolean,
  val url: String,
  val title: String?,
  val description: String?,
)
