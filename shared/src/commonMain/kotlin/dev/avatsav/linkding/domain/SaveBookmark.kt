package dev.avatsav.linkding.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SaveBookmark(
    val url: String,
    val title: String? = null,
    val description: String? = null,
    @SerialName("tag_names") val tagNames: Set<String> = emptySet(),
    @SerialName("is_archived") val isArchived: Boolean = false,
    val unread: Boolean = false,
    val shared: Boolean = false,
)
