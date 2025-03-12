package dev.avatsav.linkding.api.impl

import com.github.michaelbull.result.Result
import dev.avatsav.linkding.api.LinkdingBookmarksApi
import dev.avatsav.linkding.api.extensions.delete
import dev.avatsav.linkding.api.extensions.endpointBookmarks
import dev.avatsav.linkding.api.extensions.get
import dev.avatsav.linkding.api.extensions.parameterPage
import dev.avatsav.linkding.api.extensions.parameterQuery
import dev.avatsav.linkding.api.extensions.post
import dev.avatsav.linkding.api.extensions.toLinkdingResult
import dev.avatsav.linkding.api.models.LinkdingBookmark
import dev.avatsav.linkding.api.models.LinkdingBookmarkCategory
import dev.avatsav.linkding.api.models.LinkdingBookmarksResponse
import dev.avatsav.linkding.api.models.LinkdingCheckUrlResponse
import dev.avatsav.linkding.api.models.LinkdingError
import dev.avatsav.linkding.api.models.LinkdingErrorResponse
import dev.avatsav.linkding.api.models.LinkdingSaveBookmarkRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.parameter
import io.ktor.client.request.setBody

internal class DefaultLinkdingBookmarksApi(private val httpClient: HttpClient) :
  LinkdingBookmarksApi {
  override suspend fun getBookmarks(
    offset: Int,
    limit: Int,
    query: String,
    category: LinkdingBookmarkCategory,
    tags: List<String>,
  ): Result<LinkdingBookmarksResponse, LinkdingError> =
    httpClient
      .get<LinkdingBookmarksResponse, LinkdingErrorResponse> {
        val paths =
          if (category == LinkdingBookmarkCategory.Archived) arrayOf("archived") else emptyArray()
        endpointBookmarks(*paths)
        parameterPage(offset, limit)
        parameterQuery(query, category, tags)
      }
      .toLinkdingResult()

  override suspend fun getBookmark(id: Long): Result<LinkdingBookmark, LinkdingError> =
    httpClient
      .get<LinkdingBookmark, LinkdingErrorResponse> { endpointBookmarks(id.toString()) }
      .toLinkdingResult()

  override suspend fun saveBookmark(
    saveBookmark: LinkdingSaveBookmarkRequest
  ): Result<LinkdingBookmark, LinkdingError> =
    httpClient
      .post<LinkdingBookmark, LinkdingErrorResponse> {
        endpointBookmarks()
        setBody(saveBookmark)
      }
      .toLinkdingResult()

  override suspend fun checkUrl(url: String): Result<LinkdingCheckUrlResponse, LinkdingError> =
    httpClient
      .get<LinkdingCheckUrlResponse, LinkdingErrorResponse> {
        endpointBookmarks("check")
        parameter("url", url)
      }
      .toLinkdingResult()

  override suspend fun archiveBookmark(id: Long): Result<Unit, LinkdingError> =
    httpClient
      .post<Unit, LinkdingErrorResponse> { endpointBookmarks(id.toString(), "archive") }
      .toLinkdingResult()

  override suspend fun unarchiveBookmark(id: Long): Result<Unit, LinkdingError> =
    httpClient
      .post<Unit, LinkdingErrorResponse> { endpointBookmarks(id.toString(), "unarchive") }
      .toLinkdingResult()

  override suspend fun deleteBookmark(id: Long): Result<Unit, LinkdingError> =
    httpClient
      .delete<Unit, LinkdingErrorResponse> { endpointBookmarks(id.toString()) }
      .toLinkdingResult()
}
