package dev.avatsav.linkding.domain

import kotlinx.serialization.Serializable

@Serializable
data class LinkdingErrorResponse(
    val detail: String?
)
