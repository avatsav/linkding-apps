package dev.avatsav.linkding.data.model

sealed interface ApiConfiguration {
    data class Linkding(val hostUrl: String, val apiKey: String) : ApiConfiguration
    data object NotSet : ApiConfiguration
}
