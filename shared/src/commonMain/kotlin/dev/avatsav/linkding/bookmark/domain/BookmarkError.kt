package dev.avatsav.linkding.bookmark.domain

sealed class BookmarkError {
    object CredentialsNotSetup : BookmarkError()
    data class CouldNotGetBookmark(val message: LinkdingErrorResponse): BookmarkError()
}