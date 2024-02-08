package dev.avatsav.linkding.api.models

import kotlinx.serialization.Serializable

@Serializable
internal data class LinkdingErrorResponse(
    val detail: String = "Error processing request",
)
