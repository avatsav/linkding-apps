package dev.avatsav.linkding.bookmark.domain

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Bookmark(
    val id: Long,
    val url: String,
    val title: String,
    val description: String? = null,
    @SerialName("website_title") val websiteTitle: String? = null,
    @SerialName("website_description") val websiteDescription: String? = null,
    @SerialName("is_archived") val isArchived: Boolean? = false,
    val unread: Boolean? = false,
    val shared: Boolean? = false,
    @SerialName("tag_names") val tagNames: Set<String>? = emptySet(),
    @SerialName("date_added") val dateAdded: Instant? = Clock.System.now(),
    @SerialName("date_modified") val dateModified: Instant? = Clock.System.now(),
)
