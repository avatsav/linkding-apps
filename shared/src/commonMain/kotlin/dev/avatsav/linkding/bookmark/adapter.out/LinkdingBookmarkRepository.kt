package dev.avatsav.linkding.bookmark.adapter.out

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import dev.avatsav.linkding.bookmark.application.ports.out.BookmarkRepository
import dev.avatsav.linkding.bookmark.domain.Bookmark
import dev.avatsav.linkding.bookmark.domain.BookmarkError
import dev.avatsav.linkding.bookmark.domain.BookmarkFilter
import dev.avatsav.linkding.bookmark.domain.BookmarkList
import dev.avatsav.linkding.bookmark.domain.BookmarkSaveError
import dev.avatsav.linkding.bookmark.domain.LinkdingErrorResponse
import dev.avatsav.linkding.bookmark.domain.SaveBookmark
import dev.avatsav.linkding.extensions.ApiResponse
import dev.avatsav.linkding.extensions.requestApiResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.headers
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.Url
import io.ktor.http.contentType
import io.ktor.http.encodedPath

class LinkdingBookmarkRepository(private val httpClient: HttpClient) : BookmarkRepository {

    override suspend fun fetch(
        baseUrl: String,
        token: String,
        startIndex: Int,
        limit: Int,
        filter: BookmarkFilter,
        query: String
    ): Either<BookmarkError, BookmarkList> {

        val apiResponse: ApiResponse<BookmarkList, LinkdingErrorResponse> =
            httpClient.requestApiResponse {
                method = HttpMethod.Get
                headers {
                    append(HttpHeaders.Authorization, "Token $token")
                }
                url {
                    val url = Url(baseUrl)
                    protocol = url.protocol
                    host = url.host
                    encodedPath = "api/bookmarks" + filter.urlSuffix
                    parameters.append("startIndex", startIndex.toString())
                    parameters.append("limit", limit.toString())

                    val filterQuery = when (filter) {
                        BookmarkFilter.Unread -> "!unread "
                        BookmarkFilter.Untagged -> "!untagged "
                        BookmarkFilter.None, BookmarkFilter.Archived -> ""
                    }
                    val queryString = filterQuery + query
                    if (query.isNotEmpty()) {
                        parameters.append("q", queryString)
                    }
                }
                contentType(ContentType.Application.Json)
            }
        return when (apiResponse) {
            is ApiResponse.Success -> return apiResponse.body.right()
            is ApiResponse.Error -> {
                val errorMessage =
                    apiResponse.body?.detail ?: apiResponse.message ?: "No error message"
                BookmarkError.CouldNotGetBookmark(errorMessage).left()
            }
        }
    }

    override suspend fun fetch(
        baseUrl: String, token: String, id: Long
    ): Either<BookmarkError, Bookmark> {
        val apiResponse = httpClient.requestApiResponse<Bookmark, LinkdingErrorResponse> {
            method = HttpMethod.Get
            headers {
                append(HttpHeaders.Authorization, "Token $token")
            }
            url {
                val url = Url(baseUrl)
                protocol = url.protocol
                host = url.host
                encodedPath = "api/bookmarks/${id}/"
            }
            contentType(ContentType.Application.Json)
        }
        return when (apiResponse) {
            is ApiResponse.Success -> return apiResponse.body.right()
            is ApiResponse.Error -> {
                val errorMessage =
                    apiResponse.body?.detail ?: apiResponse.message ?: "No error message"
                BookmarkError.CouldNotGetBookmark(errorMessage).left()
            }
        }
    }

    override suspend fun save(
        baseUrl: String, token: String, saveBookmark: SaveBookmark
    ): Either<BookmarkSaveError, Bookmark> {
        val apiResponse = httpClient.requestApiResponse<Bookmark, LinkdingErrorResponse> {
            method = HttpMethod.Post
            headers {
                append(HttpHeaders.Authorization, "Token $token")
            }
            url {
                val url = Url(baseUrl)
                protocol = url.protocol
                host = url.host
                encodedPath = "/api/bookmarks/"
            }
            contentType(ContentType.Application.Json)
            setBody(saveBookmark)
        }
        return when (apiResponse) {
            is ApiResponse.Success -> return apiResponse.body.right()
            is ApiResponse.Error -> {
                val errorMessage =
                    apiResponse.body?.detail ?: apiResponse.message ?: "No error message"
                BookmarkSaveError.CouldNotSaveBookmark(errorMessage).left()
            }
        }
    }
}