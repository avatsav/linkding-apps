package dev.avatsav.linkding.domain

sealed class TagError {
    object ConfigurationNotSetup : TagError()
}