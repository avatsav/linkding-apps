package dev.avatsav.linkding.domain

import kotlinx.serialization.Serializable

@Serializable
data class BookmarkList(
    val count: Long,
    val next: String? = null,
    val previous: String? = null,
    val results: List<Bookmark>
)
