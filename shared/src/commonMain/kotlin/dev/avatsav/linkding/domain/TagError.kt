package dev.avatsav.linkding.domain

sealed class TagError {
    data class CouldNotGetTag(val message: String) : TagError()
    object ConfigurationNotSetup : TagError()
}
