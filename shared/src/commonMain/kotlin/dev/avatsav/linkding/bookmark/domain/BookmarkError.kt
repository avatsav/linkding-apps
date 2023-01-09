package dev.avatsav.linkding.bookmark.domain

import kotlinx.serialization.Serializable

@Serializable
sealed class BookmarkError {
    object ConfigurationNotSetup : BookmarkError()
    data class CouldNotGetBookmark(val message: String) : BookmarkError()
}