package dev.avatsav.linkding.api.models

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LinkdingBookmark(
    val id: Long,
    val url: String,
    val title: String = "",
    val description: String = "null",
    @SerialName("website_title") val websiteTitle: String = "",
    @SerialName("website_description") val websiteDescription: String = "",
    @SerialName("is_archived") val isArchived: Boolean = false,
    val unread: Boolean = false,
    val shared: Boolean = false,
    @SerialName("tag_names") val tagNames: Set<String> = emptySet(),
    @SerialName("date_added") val dateAdded: Instant? = null,
    @SerialName("date_modified") val dateModified: Instant? = null,
) {
    fun getSafeTitle(): String = if (title.isNotBlank()) {
        title
    } else if (websiteTitle.isNotBlank()) {
        websiteTitle
    } else {
        ""
    }

    fun getSafeDescription(): String = if (description.isNotBlank()) {
        description
    } else if (websiteDescription.isNotBlank()) {
        websiteDescription
    } else {
        ""
    }
}
