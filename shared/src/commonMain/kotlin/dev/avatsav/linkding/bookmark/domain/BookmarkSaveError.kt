package dev.avatsav.linkding.bookmark.domain

import kotlinx.serialization.Serializable

@Serializable
sealed class BookmarkSaveError {
    object ConfigurationNotSetup : BookmarkSaveError()
    data class CouldNotSaveBookmark(val message: String) : BookmarkSaveError()
}