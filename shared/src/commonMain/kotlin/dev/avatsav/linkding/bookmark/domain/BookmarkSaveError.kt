package dev.avatsav.linkding.bookmark.domain

import kotlinx.serialization.Serializable

@Serializable
sealed class BookmarkSaveError {
    object CredentialsNotSetup : BookmarkSaveError()
    data class CouldNotSaveBookmark(val message: LinkdingErrorResponse) : BookmarkSaveError()
}