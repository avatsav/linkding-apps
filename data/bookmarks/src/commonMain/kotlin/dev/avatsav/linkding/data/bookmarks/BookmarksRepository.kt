package dev.avatsav.linkding.data.bookmarks

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapEither
import com.github.michaelbull.result.mapError
import dev.avatsav.linkding.api.LinkdingBookmarksApi
import dev.avatsav.linkding.api.models.LinkdingBookmarkFilter
import dev.avatsav.linkding.data.bookmarks.mappers.BookmarkErrorMapper
import dev.avatsav.linkding.data.bookmarks.mappers.BookmarkMapper
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.data.model.BookmarkError
import dev.avatsav.linkding.data.model.BookmarksResult
import dev.avatsav.linkding.data.model.SaveBookmark
import me.tatarka.inject.annotations.Inject

@Inject
class BookmarksRepository(
    private val bookmarksApi: LinkdingBookmarksApi,
    private val bookmarkMapper: BookmarkMapper,
    private val errorMapper: BookmarkErrorMapper,
) {
    suspend fun getBookmarks(
        offset: Int,
        limit: Int,
        filter: LinkdingBookmarkFilter = LinkdingBookmarkFilter.None,
        query: String = "",
    ): Result<BookmarksResult, BookmarkError> {
        return bookmarksApi.getBookmarks(offset, limit, filter, query).mapEither(
            success = bookmarkMapper::map,
            failure = errorMapper::map,
        )
    }

    suspend fun getArchived(
        offset: Int,
        limit: Int,
        query: String,
    ): Result<BookmarksResult, BookmarkError> {
        return bookmarksApi.getArchived(offset, limit, query).mapEither(
            success = bookmarkMapper::map,
            failure = errorMapper::map,
        )
    }

    suspend fun getBookmark(id: Long): Result<Bookmark, BookmarkError> {
        return bookmarksApi.getBookmark(id).mapEither(
            success = bookmarkMapper::map,
            failure = errorMapper::map,
        )
    }

    suspend fun saveBookmark(saveBookmark: SaveBookmark): Result<Bookmark, BookmarkError> {
        val request = bookmarkMapper.map(saveBookmark)
        return bookmarksApi.saveBookmark(request).mapEither(
            success = bookmarkMapper::map,
            failure = errorMapper::map,
        )
    }

    suspend fun archiveBookmark(id: Long): Result<Unit, BookmarkError> {
        return bookmarksApi.archiveBookmark(id).mapError(errorMapper::map)
    }

    suspend fun unarchiveBookmark(id: Long): Result<Unit, BookmarkError> {
        return bookmarksApi.unarchiveBookmark(id).mapError(errorMapper::map)
    }

    suspend fun deleteBookmark(id: Long): Result<Unit, BookmarkError> {
        return bookmarksApi.deleteBookmark(id).mapError(errorMapper::map)
    }
}
