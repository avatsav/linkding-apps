package dev.avatsav.linkding.api

import com.github.michaelbull.result.Result
import dev.avatsav.linkding.api.models.LinkdingBookmark
import dev.avatsav.linkding.api.models.LinkdingBookmarkFilter
import dev.avatsav.linkding.api.models.LinkdingBookmarksResponse
import dev.avatsav.linkding.api.models.LinkdingErrorResponse
import dev.avatsav.linkding.api.models.LinkdingSaveBookmarkRequest

interface LinkdingBookmarksApi {
    suspend fun getBookmarks(
        offset: Int,
        limit: Int,
        filter: LinkdingBookmarkFilter,
        query: String,
    ): Result<LinkdingBookmarksResponse, LinkdingErrorResponse>

    suspend fun getArchived(
        offset: Int,
        limit: Int,
        query: String,
    ): Result<LinkdingBookmarksResponse, LinkdingErrorResponse>

    suspend fun getBookmark(id: Long): Result<LinkdingBookmark, LinkdingErrorResponse>

    suspend fun saveBookmark(saveBookmark: LinkdingSaveBookmarkRequest): Result<LinkdingBookmark, LinkdingErrorResponse>

    suspend fun archiveBookmark(id: Long): Result<Unit, LinkdingErrorResponse>

    suspend fun unarchiveBookmark(id: Long): Result<Unit, LinkdingErrorResponse>

    suspend fun deleteBookmark(id: Long): Result<Unit, LinkdingErrorResponse>
}
