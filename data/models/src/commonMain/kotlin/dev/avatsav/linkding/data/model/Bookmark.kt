package dev.avatsav.linkding.data.model

import kotlinx.datetime.Instant

data class Bookmark(
    val id: Long,
    val url: String,
    val urlHost: String,
    val title: String,
    val description: String,
    val archived: Boolean = false,
    val unread: Boolean = false,
    val tags: Set<String> = emptySet(),
    val added: Instant? = null,
    val modified: Instant? = null,
)
