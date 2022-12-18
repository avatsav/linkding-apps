package dev.avatsav.linkding.bookmark.domain

import kotlinx.serialization.Serializable

@Serializable
data class LinkdingErrorResponse(
    val detail: String
)
