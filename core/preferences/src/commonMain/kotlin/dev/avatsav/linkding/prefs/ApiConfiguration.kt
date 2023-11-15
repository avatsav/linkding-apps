package dev.avatsav.linkding.prefs

sealed interface ApiConfiguration {
    data class Linkding(val hostUrl: String, val apiKey: String) : ApiConfiguration
    data object NotSet : ApiConfiguration
}
