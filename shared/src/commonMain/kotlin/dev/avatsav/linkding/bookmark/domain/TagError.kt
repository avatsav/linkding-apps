package dev.avatsav.linkding.bookmark.domain

sealed class TagError {
    object ConfigurationNotSetup : TagError()
}