package dev.avatsav.linkding.data.bookmarks

import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapEither
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.onSuccess
import dev.avatsav.linkding.api.LinkdingApiProvider
import dev.avatsav.linkding.data.bookmarks.mappers.BookmarkErrorMapper
import dev.avatsav.linkding.data.bookmarks.mappers.BookmarkMapper
import dev.avatsav.linkding.data.bookmarks.mappers.CheckUrlResultMapper
import dev.avatsav.linkding.data.db.daos.BookmarksDao
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.data.model.BookmarkCategory
import dev.avatsav.linkding.data.model.BookmarkError
import dev.avatsav.linkding.data.model.CheckUrlResult
import dev.avatsav.linkding.data.model.SaveBookmark
import dev.avatsav.linkding.data.model.Tag
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class BookmarksRepository(
    private val apiProvider: Lazy<LinkdingApiProvider>,
    private val bookmarksDao: BookmarksDao,
    private val bookmarksPagingDataFactory: BookmarksPagingDataFactory,
    private val searchBookmarksPagingSourceFactory: SearchBookmarksPagingSourceFactory,
    private val bookmarkMapper: BookmarkMapper,
    private val checkUrlMapper: CheckUrlResultMapper,
    private val errorMapper: BookmarkErrorMapper,
) {

    fun getBookmarksPaged(
        pagingConfig: PagingConfig,
        category: BookmarkCategory,
        selectedTags: List<Tag>,
    ): Flow<PagingData<Bookmark>> {
        return bookmarksPagingDataFactory(pagingConfig, category, selectedTags)
    }

    fun searchBookmarks(
        pagingConfig: PagingConfig,
        query: String,
    ): Flow<PagingData<Bookmark>> {
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { searchBookmarksPagingSourceFactory(query) },
        ).flow
    }

    suspend fun checkUrl(url: String): Result<CheckUrlResult, BookmarkError> {
        return apiProvider.value.bookmarksApi.checkUrl(url).mapEither(
            success = checkUrlMapper::map,
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
        return apiProvider.value.bookmarksApi.archiveBookmark(id)
            .onSuccess { bookmarksDao.delete(id) }
            .mapError(errorMapper::map)
    }

    suspend fun unarchiveBookmark(id: Long): Result<Unit, BookmarkError> {
        return apiProvider.value.bookmarksApi.unarchiveBookmark(id)
            .onSuccess { bookmarksDao.delete(id) }
            .mapError(errorMapper::map)
    }

    suspend fun deleteBookmark(id: Long): Result<Unit, BookmarkError> {
        return apiProvider.value.bookmarksApi.deleteBookmark(id)
            .onSuccess { bookmarksDao.delete(id) }
            .mapError(errorMapper::map)
    }
}
