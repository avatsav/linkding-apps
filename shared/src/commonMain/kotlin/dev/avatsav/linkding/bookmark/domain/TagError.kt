package dev.avatsav.linkding.bookmark.domain

sealed class TagError {
    object CredentialsNotSetup : TagError()
}