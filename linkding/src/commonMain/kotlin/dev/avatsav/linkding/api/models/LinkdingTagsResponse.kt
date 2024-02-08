package dev.avatsav.linkding.api.models

import kotlinx.serialization.Serializable

@Serializable
data class LinkdingTagsResponse(
    val count: Long,
    val next: String?,
    val previous: String?,
    val results: List<LinkdingTag>,
)
