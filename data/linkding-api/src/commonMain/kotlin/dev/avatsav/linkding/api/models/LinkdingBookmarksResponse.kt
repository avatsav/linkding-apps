package dev.avatsav.linkding.api.models

import kotlinx.serialization.Serializable

@Serializable
data class LinkdingBookmarksResponse(
  val count: Long,
  val next: String? = null,
  val previous: String? = null,
  val results: List<LinkdingBookmark>,
)
