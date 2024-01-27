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
import dev.avatsav.linkding.data.model.SaveBookmark

class BookmarksRepository(
    private val bookmarksApi: LinkdingBookmarksApi,
    private val bookmarkMapper: BookmarkMapper,
    private val errorMapper: BookmarkErrorMapper,
) {
    suspend fun getBookmarks(
        offset: Int,
        limit: Int,
        filter: LinkdingBookmarkFilter,
        query: String,
    ): Result<List<Bookmark>, BookmarkError> {
        return bookmarksApi.getBookmarks(offset, limit, filter, query).mapEither(
            success = bookmarkMapper::map,
            failure = { errorMapper.map(it) },
        )
    }

    suspend fun getArchived(
        offset: Int,
        limit: Int,
        query: String,
    ): Result<List<Bookmark>, BookmarkError> {
        return bookmarksApi.getArchived(offset, limit, query).mapEither(
            success = bookmarkMapper::map,
            failure = { errorMapper.map(it) },
        )
    }

    suspend fun getBookmark(id: Long): Result<Bookmark, BookmarkError> {
        return bookmarksApi.getBookmark(id).mapEither(
            success = bookmarkMapper::map,
            failure = { errorMapper.map(it) },
        )
    }

    suspend fun saveBookmark(saveBookmark: SaveBookmark): Result<Bookmark, BookmarkError> {
        val request = bookmarkMapper.map(saveBookmark)
        return bookmarksApi.saveBookmark(request).mapEither(
            success = bookmarkMapper::map,
            failure = { errorMapper.map(it) },
        )
    }

    suspend fun archiveBookmark(id: Long): Result<Unit, BookmarkError> {
        return bookmarksApi.archiveBookmark(id).mapError { errorMapper.map(it) }
    }

    suspend fun unarchiveBookmark(id: Long): Result<Unit, BookmarkError> {
        return bookmarksApi.unarchiveBookmark(id).mapError { errorMapper.map(it) }
    }

    suspend fun deleteBookmark(id: Long): Result<Unit, BookmarkError> {
        return bookmarksApi.deleteBookmark(id).mapError { errorMapper.map(it) }
    }

}
