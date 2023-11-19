package dev.avatsav.linkding.api

import arrow.core.Either
import dev.avatsav.linkding.api.extensions.delete
import dev.avatsav.linkding.api.extensions.endpointBookmarks
import dev.avatsav.linkding.api.extensions.get
import dev.avatsav.linkding.api.extensions.parameterPage
import dev.avatsav.linkding.api.extensions.parameterQuery
import dev.avatsav.linkding.api.extensions.parameterQueryWithFilter
import dev.avatsav.linkding.api.extensions.post
import dev.avatsav.linkding.api.extensions.toEither
import dev.avatsav.linkding.api.models.LinkdingBookmark
import dev.avatsav.linkding.api.models.LinkdingBookmarkFilter
import dev.avatsav.linkding.api.models.LinkdingBookmarksResponse
import dev.avatsav.linkding.api.models.LinkdingErrorResponse
import dev.avatsav.linkding.api.models.LinkdingSaveBookmarkRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.setBody

class DefaultLinkdingBookmarksApi(private val httpClient: HttpClient) : LinkdingBookmarksApi {
    override suspend fun getBookmarks(
        offset: Int,
        limit: Int,
        filter: LinkdingBookmarkFilter,
        query: String,
    ): Either<LinkdingErrorResponse, LinkdingBookmarksResponse> {
        return httpClient.get<LinkdingBookmarksResponse, LinkdingErrorResponse> {
            endpointBookmarks()
            parameterPage(offset, limit)
            parameterQueryWithFilter(query, filter)
        }.toEither(LinkdingErrorResponse.DEFAULT)
    }

    override suspend fun getArchived(
        offset: Int,
        limit: Int,
        query: String,
    ): Either<LinkdingErrorResponse, LinkdingBookmarksResponse> {
        return httpClient.get<LinkdingBookmarksResponse, LinkdingErrorResponse> {
            endpointBookmarks("archived")
            parameterPage(offset, limit)
            parameterQuery(query)
        }.toEither(LinkdingErrorResponse.DEFAULT)
    }

    override suspend fun getBookmark(id: Long): Either<LinkdingErrorResponse, LinkdingBookmark> {
        return httpClient.get<LinkdingBookmark, LinkdingErrorResponse> {
            endpointBookmarks(id.toString())
        }.toEither(LinkdingErrorResponse.DEFAULT)
    }

    override suspend fun saveBookmark(saveBookmark: LinkdingSaveBookmarkRequest): Either<LinkdingErrorResponse, LinkdingBookmark> {
        return httpClient.post<LinkdingBookmark, LinkdingErrorResponse> {
            endpointBookmarks()
            setBody(saveBookmark)
        }.toEither(LinkdingErrorResponse.DEFAULT)
    }

    override suspend fun archiveBookmark(id: Long): Either<LinkdingErrorResponse, Unit> {
        return httpClient.post<Unit, LinkdingErrorResponse> {
            endpointBookmarks(id.toString(), "archive")
        }.toEither(LinkdingErrorResponse.DEFAULT)
    }

    override suspend fun unarchiveBookmark(id: Long): Either<LinkdingErrorResponse, Unit> {
        return httpClient.post<Unit, LinkdingErrorResponse> {
            endpointBookmarks(id.toString(), "unarchive")
        }.toEither(LinkdingErrorResponse.DEFAULT)
    }

    override suspend fun deleteBookmark(id: Long): Either<LinkdingErrorResponse, Unit> {
        return httpClient.delete<Unit, LinkdingErrorResponse> {
            endpointBookmarks(id.toString())
        }.toEither(LinkdingErrorResponse.DEFAULT)
    }
}
