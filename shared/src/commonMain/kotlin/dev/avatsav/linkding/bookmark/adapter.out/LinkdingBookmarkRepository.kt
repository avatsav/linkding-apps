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
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

class LinkdingBookmarkRepository(private val httpClient: HttpClient) : BookmarkRepository {
    override suspend fun fetch(
        baseUrl: String,
        token: String,
        startIndex: Int,
        limit: Int,
        filter: BookmarkFilter,
        query: String
    ): Either<BookmarkError, BookmarkList> {
        val response = httpClient.get("${baseUrl}/api/bookmarks" + filter.urlSuffix) {
            headers {
                append(HttpHeaders.Authorization, "Token $token")
            }
            url {
                parameters.append("startIndex", startIndex.toString())
                parameters.append("limit", limit.toString())
                parameters.append("q", query)
            }
        }
        if (response.status == HttpStatusCode.OK) {
            val bookmarkList: BookmarkList = response.body()
            return bookmarkList.right()
        }
        val errorResponse: LinkdingErrorResponse = response.body()
        return BookmarkError.CouldNotGetBookmark(errorResponse).left()
    }

    override suspend fun fetch(
        baseUrl: String,
        token: String,
        id: Long
    ): Either<BookmarkError, Bookmark> {
        val response = httpClient.get("${baseUrl}/api/bookmarks/${id}/") {
            headers {
                append(HttpHeaders.Authorization, "Token $token")
            }
        }
        if (response.status == HttpStatusCode.OK) {
            val bookmark: Bookmark = response.body()
            return bookmark.right()
        }
        val errorResponse: LinkdingErrorResponse = response.body()
        return BookmarkError.CouldNotGetBookmark(errorResponse).left()
    }

    override suspend fun save(
        baseUrl: String,
        token: String,
        saveBookmark: SaveBookmark
    ): Either<BookmarkSaveError, Bookmark> {
        val response = httpClient.post("${baseUrl}/api/bookmarks/") {
            headers {
                append(HttpHeaders.Authorization, "Token $token")
            }
            contentType(ContentType.Application.Json)
            setBody(saveBookmark)
        }
        if (response.status == HttpStatusCode.Created) {
            val bookmark: Bookmark = response.body()
            return bookmark.right()
        }
        val errorResponse: LinkdingErrorResponse = response.body()
        return BookmarkSaveError.CouldNotSaveBookmark(errorResponse).left()
    }
}