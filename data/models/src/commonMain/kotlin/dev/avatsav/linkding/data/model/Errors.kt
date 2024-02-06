package dev.avatsav.linkding.data.model

data class BookmarkError(val message: String)

sealed class ConfigurationError(open val message: String) {
    data class InvalidHostname(override val message: String) : ConfigurationError(message)
    data class InvalidApiKey(override val message: String) : ConfigurationError(message)
    data class Other(override val message: String) : ConfigurationError(message)

}
