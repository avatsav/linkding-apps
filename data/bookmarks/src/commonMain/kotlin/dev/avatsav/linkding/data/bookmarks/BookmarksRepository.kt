package dev.avatsav.linkding.data.bookmarks

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapEither
import com.github.michaelbull.result.mapError
import dev.avatsav.linkding.api.LinkdingApiProvider
import dev.avatsav.linkding.data.bookmarks.mappers.BookmarkErrorMapper
import dev.avatsav.linkding.data.bookmarks.mappers.BookmarkMapper
import dev.avatsav.linkding.data.bookmarks.mappers.CheckUrlResultMapper
import dev.avatsav.linkding.data.bookmarks.mappers.toLinkding
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.data.model.BookmarkCategory
import dev.avatsav.linkding.data.model.BookmarkError
import dev.avatsav.linkding.data.model.BookmarksResult
import dev.avatsav.linkding.data.model.CheckUrlResult
import dev.avatsav.linkding.data.model.SaveBookmark
import me.tatarka.inject.annotations.Inject

@Inject
class BookmarksRepository(
    private val apiProvider: Lazy<LinkdingApiProvider>,
    private val bookmarkMapper: BookmarkMapper,
    private val checkUrlMapper: CheckUrlResultMapper,
    private val errorMapper: BookmarkErrorMapper,
) {
    suspend fun getBookmarks(
        offset: Int,
        limit: Int,
        query: String = "",
        category: BookmarkCategory = BookmarkCategory.All,
    ): Result<BookmarksResult, BookmarkError> {
        return apiProvider.value.bookmarksApi.getBookmarks(
            offset,
            limit,
            query,
            category.toLinkding(),
        ).mapEither(
            success = bookmarkMapper::map,
            failure = errorMapper::map,
        )
    }

    suspend fun checkUrl(url: String): Result<CheckUrlResult, BookmarkError> {
        return apiProvider.value.bookmarksApi.checkUrl(url).mapEither(
            success = checkUrlMapper::map,
            failure = errorMapper::map,
        )
    }

    suspend fun getBookmark(id: Long): Result<Bookmark, BookmarkError> {
        return apiProvider.value.bookmarksApi.getBookmark(id).mapEither(
            success = bookmarkMapper::map,
            failure = errorMapper::map,
        )
    }

    suspend fun saveBookmark(saveBookmark: SaveBookmark): Result<Bookmark, BookmarkError> {
        val request = bookmarkMapper.map(saveBookmark)
        return apiProvider.value.bookmarksApi.saveBookmark(request).mapEither(
            success = bookmarkMapper::map,
            failure = errorMapper::map,
        )
    }

    suspend fun archiveBookmark(id: Long): Result<Unit, BookmarkError> {
        return apiProvider.value.bookmarksApi.archiveBookmark(id).mapError(errorMapper::map)
    }

    suspend fun unarchiveBookmark(id: Long): Result<Unit, BookmarkError> {
        return apiProvider.value.bookmarksApi.unarchiveBookmark(id).mapError(errorMapper::map)
    }

    suspend fun deleteBookmark(id: Long): Result<Unit, BookmarkError> {
        return apiProvider.value.bookmarksApi.deleteBookmark(id).mapError(errorMapper::map)
    }
}
