package dev.avatsav.linkding.bookmarks.api

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.github.michaelbull.result.Result
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.data.model.BookmarkCategory
import dev.avatsav.linkding.data.model.BookmarkError
import dev.avatsav.linkding.data.model.CheckUrlResult
import dev.avatsav.linkding.data.model.SaveBookmark
import dev.avatsav.linkding.data.model.UpdateBookmark
import kotlinx.coroutines.flow.Flow

interface BookmarksRepository {
  fun getBookmarksPaged(
    cached: Boolean,
    pagingConfig: PagingConfig,
    query: String,
    category: BookmarkCategory,
    tags: List<String>,
  ): Flow<PagingData<Bookmark>>

  suspend fun checkUrl(url: String): Result<CheckUrlResult, BookmarkError>

  suspend fun getBookmark(id: Long): Result<Bookmark, BookmarkError>

  suspend fun saveBookmark(saveBookmark: SaveBookmark): Result<Bookmark, BookmarkError>

  suspend fun updateBookmark(updateBookmark: UpdateBookmark): Result<Bookmark, BookmarkError>

  suspend fun archiveBookmark(id: Long): Result<Unit, BookmarkError>

  suspend fun unarchiveBookmark(id: Long): Result<Unit, BookmarkError>

  suspend fun deleteBookmark(id: Long): Result<Unit, BookmarkError>
}
