package dev.avatsav.linkding.api.models

import kotlinx.serialization.Serializable

@Serializable
data class LinkdingErrorResponse(
    val detail: String = "Error processing request",
) {
    companion object {
        val DEFAULT = LinkdingErrorResponse()
    }
}
