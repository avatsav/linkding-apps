package dev.avatsav.linkding.prefs

sealed interface ApiConfiguration {
    data class Linkding(val host: String, val apiKey: String) : ApiConfiguration
    data object NotSet : ApiConfiguration
}
