package dev.avatsav.linkding.bookmark.domain

import kotlinx.serialization.Serializable

@Serializable
sealed class BookmarkError {
    object CredentialsNotSetup : BookmarkError()
    data class CouldNotGetBookmark(val message: LinkdingErrorResponse) : BookmarkError()
}