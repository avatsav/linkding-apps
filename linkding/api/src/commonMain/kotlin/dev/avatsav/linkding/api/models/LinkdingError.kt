package dev.avatsav.linkding.api.models

sealed class LinkdingError(open val message: String) {
    data class Connectivity(override val message: String = "Unauthorized") : LinkdingError(message)

    data class Unauthorized(override val message: String = "Unauthorized") : LinkdingError(message)

    data class Other(override val message: String) : LinkdingError(message)
}
