package dev.avatsav.linkding.bookmark.domain

import kotlinx.serialization.Serializable

@Serializable
data class TagList(
    val count: Long,
    val next: String?,
    val previous: String?,
    val results: List<Tag>
)
