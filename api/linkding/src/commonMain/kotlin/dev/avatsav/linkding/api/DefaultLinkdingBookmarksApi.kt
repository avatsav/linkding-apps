package dev.avatsav.linkding.api

import com.github.michaelbull.result.Result
import dev.avatsav.linkding.api.extensions.delete
import dev.avatsav.linkding.api.extensions.endpointBookmarks
import dev.avatsav.linkding.api.extensions.get
import dev.avatsav.linkding.api.extensions.parameterPage
import dev.avatsav.linkding.api.extensions.parameterQuery
import dev.avatsav.linkding.api.extensions.parameterQueryWithFilter
import dev.avatsav.linkding.api.extensions.post
import dev.avatsav.linkding.api.extensions.toResult
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
    ): Result<LinkdingBookmarksResponse, LinkdingErrorResponse> {
        return httpClient.get<LinkdingBookmarksResponse, LinkdingErrorResponse> {
            endpointBookmarks()
            parameterPage(offset, limit)
            parameterQueryWithFilter(query, filter)
        }.toResult(LinkdingErrorResponse.DEFAULT)
    }

    override suspend fun getArchived(
        offset: Int,
        limit: Int,
        query: String,
    ): Result<LinkdingBookmarksResponse, LinkdingErrorResponse> {
        return httpClient.get<LinkdingBookmarksResponse, LinkdingErrorResponse> {
            endpointBookmarks("archived")
            parameterPage(offset, limit)
            parameterQuery(query)
        }.toResult(LinkdingErrorResponse.DEFAULT)
    }

    override suspend fun getBookmark(id: Long): Result<LinkdingBookmark, LinkdingErrorResponse> {
        return httpClient.get<LinkdingBookmark, LinkdingErrorResponse> {
            endpointBookmarks(id.toString())
        }.toResult(LinkdingErrorResponse.DEFAULT)
    }

    override suspend fun saveBookmark(saveBookmark: LinkdingSaveBookmarkRequest): Result<LinkdingBookmark, LinkdingErrorResponse> {
        return httpClient.post<LinkdingBookmark, LinkdingErrorResponse> {
            endpointBookmarks()
            setBody(saveBookmark)
        }.toResult(LinkdingErrorResponse.DEFAULT)
    }

    override suspend fun archiveBookmark(id: Long): Result<Unit, LinkdingErrorResponse> {
        return httpClient.post<Unit, LinkdingErrorResponse> {
            endpointBookmarks(id.toString(), "archive")
        }.toResult(LinkdingErrorResponse.DEFAULT)
    }

    override suspend fun unarchiveBookmark(id: Long): Result<Unit, LinkdingErrorResponse> {
        return httpClient.post<Unit, LinkdingErrorResponse> {
            endpointBookmarks(id.toString(), "unarchive")
        }.toResult(LinkdingErrorResponse.DEFAULT)
    }

    override suspend fun deleteBookmark(id: Long): Result<Unit, LinkdingErrorResponse> {
        return httpClient.delete<Unit, LinkdingErrorResponse> {
            endpointBookmarks(id.toString())
        }.toResult(LinkdingErrorResponse.DEFAULT)
    }
}
