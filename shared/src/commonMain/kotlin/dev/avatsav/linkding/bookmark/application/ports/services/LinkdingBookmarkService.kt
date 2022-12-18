package dev.avatsav.linkding.bookmark.application.ports.services

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import dev.avatsav.linkding.bookmark.application.ports.`in`.BookmarkService
import dev.avatsav.linkding.bookmark.domain.BookmarkError
import dev.avatsav.linkding.bookmark.domain.BookmarkList
import dev.avatsav.linkding.bookmark.domain.LinkdingErrorResponse
import dev.avatsav.linkding.data.Credentials
import dev.avatsav.linkding.data.CredentialsStore
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode

class LinkdingBookmarkService(private val httpClient: HttpClient, private val credentialsStore: CredentialsStore) :
    BookmarkService {

    override suspend fun getBookmarks(
        startIndex: Int,
        limit: Int,
        query: String
    ): Either<BookmarkError, BookmarkList> {
        return credentialsStore.get().toEither()
            .map { credentials ->
                return@getBookmarks fetchBookmarks(
                    startIndex,
                    limit,
                    query,
                    credentials
                )
            }
            .mapLeft { return@getBookmarks BookmarkError.CredentialsNotSetup.left() }
    }

    private suspend fun fetchBookmarks(
        startIndex: Int,
        limit: Int,
        query: String,
        credentials: Credentials
    ): Either<BookmarkError, BookmarkList> {
        val response = httpClient.get("${credentials.url}/api/bookmarks/") {
            headers {
                append(HttpHeaders.Authorization, "Token ${credentials.apiKey}")
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
}