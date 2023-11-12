package dev.avatsav.linkding.api

import arrow.core.Either
import dev.avatsav.linkding.api.models.BookmarkFilter
import dev.avatsav.linkding.api.models.LinkdingBookmark
import dev.avatsav.linkding.api.models.LinkdingBookmarksResponse
import dev.avatsav.linkding.api.models.LinkdingErrorResponse
import dev.avatsav.linkding.api.models.LinkdingSaveBookmarkRequest

class DefaultLinkdingBookmarksApi : LinkdingBookmarksApi {
    override suspend fun getBookmarks(
        offset: Int,
        limit: Int,
        filter: BookmarkFilter,
        query: String,
    ): Either<LinkdingErrorResponse, LinkdingBookmarksResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getBookmark(id: Long): Either<LinkdingErrorResponse, LinkdingBookmark> {
        TODO("Not yet implemented")
    }

    override suspend fun saveBookmark(saveBookmark: LinkdingSaveBookmarkRequest): Either<LinkdingErrorResponse, LinkdingBookmark> {
        TODO("Not yet implemented")
    }

    override suspend fun archiveBookmark(id: Long): Either<LinkdingErrorResponse, Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun unarchiveBookmark(id: Long): Either<LinkdingErrorResponse, Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteBookmark(id: Long): Either<LinkdingErrorResponse, Unit> {
        TODO("Not yet implemented")
    }


}
