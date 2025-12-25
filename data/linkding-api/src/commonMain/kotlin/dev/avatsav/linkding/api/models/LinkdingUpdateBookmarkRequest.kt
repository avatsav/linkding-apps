package dev.avatsav.linkding.api.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LinkdingUpdateBookmarkRequest(
  val title: String? = null,
  val description: String? = null,
  val notes: String? = null,
  @SerialName("tag_names") val tagNames: Set<String>? = null,
)
