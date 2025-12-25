package dev.avatsav.linkding.api

import com.github.michaelbull.result.Result
import dev.avatsav.linkding.api.models.LinkdingBookmark
import dev.avatsav.linkding.api.models.LinkdingBookmarkCategory
import dev.avatsav.linkding.api.models.LinkdingBookmarksResponse
import dev.avatsav.linkding.api.models.LinkdingCheckUrlResponse
import dev.avatsav.linkding.api.models.LinkdingError
import dev.avatsav.linkding.api.models.LinkdingSaveBookmarkRequest
import dev.avatsav.linkding.api.models.LinkdingUpdateBookmarkRequest

interface LinkdingBookmarksApi {
  suspend fun getBookmarks(
    offset: Int,
    limit: Int,
    query: String = "",
    category: LinkdingBookmarkCategory = LinkdingBookmarkCategory.All,
    tags: List<String>,
  ): Result<LinkdingBookmarksResponse, LinkdingError>

  suspend fun getBookmark(id: Long): Result<LinkdingBookmark, LinkdingError>

  suspend fun saveBookmark(
    request: LinkdingSaveBookmarkRequest
  ): Result<LinkdingBookmark, LinkdingError>

  suspend fun updateBookmark(
    id: Long,
    request: LinkdingUpdateBookmarkRequest,
  ): Result<LinkdingBookmark, LinkdingError>

  suspend fun checkUrl(url: String): Result<LinkdingCheckUrlResponse, LinkdingError>

  suspend fun archiveBookmark(id: Long): Result<Unit, LinkdingError>

  suspend fun unarchiveBookmark(id: Long): Result<Unit, LinkdingError>

  suspend fun deleteBookmark(id: Long): Result<Unit, LinkdingError>
}
