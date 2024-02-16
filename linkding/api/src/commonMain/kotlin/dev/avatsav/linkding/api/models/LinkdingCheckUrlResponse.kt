package dev.avatsav.linkding.api.models

import kotlinx.serialization.Serializable

@Serializable
data class LinkdingCheckUrlResponse(
    val bookmark: LinkdingBookmark?,
    val metadata: Metadata,
) {
    @Serializable
    data class Metadata(
        val url: String,
        val title: String?,
        val description: String?,
    )
}
