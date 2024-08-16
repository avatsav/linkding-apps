package dev.avatsav.linkding.data.bookmarks.internal

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.mapEither
import dev.avatsav.linkding.AppCoroutineDispatchers
import dev.avatsav.linkding.api.LinkdingBookmarksApi
import dev.avatsav.linkding.data.bookmarks.internal.mappers.BookmarkErrorMapper
import dev.avatsav.linkding.data.bookmarks.internal.mappers.BookmarkMapper
import dev.avatsav.linkding.data.bookmarks.internal.mappers.toLinkding
import dev.avatsav.linkding.data.db.daos.PagingBookmarksDao
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.data.model.BookmarkCategory
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import kotlinx.coroutines.withContext

typealias BookmarksRemoteMediatorFactory = (String, BookmarkCategory, List<String>) -> BookmarksRemoteMediator

@OptIn(ExperimentalPagingApi::class)
@Inject
class BookmarksRemoteMediator(
    @Assisted private val query: String,
    @Assisted private val category: BookmarkCategory,
    @Assisted private val tags: List<String>,
    private val bookmarksApi: LinkdingBookmarksApi,
    private val bookmarksDao: PagingBookmarksDao,
    private val dispatchers: AppCoroutineDispatchers,
    private val bookmarkMapper: BookmarkMapper,
    private val errorMapper: BookmarkErrorMapper,
) : RemoteMediator<Int, Bookmark>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Bookmark>,
    ): MediatorResult = withContext(dispatchers.io) {
        val offset = when (loadType) {
            LoadType.REFRESH -> 0
            LoadType.PREPEND -> return@withContext MediatorResult.Success(
                endOfPaginationReached = true,
            )

            LoadType.APPEND -> {
                bookmarksDao.countBookmarks().toInt()
            }
        }

        bookmarksApi.getBookmarks(
            query = query,
            tags = tags,
            category = category.toLinkding(),
            offset = offset,
            limit = state.config.pageSize,
        ).mapEither(
            success = bookmarkMapper::map,
            failure = errorMapper::map,
        ).fold(
            success = {
                val bookmarks = it.bookmarks
                if (loadType == LoadType.REFRESH) {
                    bookmarksDao.refresh(bookmarks)
                } else {
                    bookmarksDao.append(bookmarks)
                }
                MediatorResult.Success(endOfPaginationReached = it.nextPage == null)
            },
            failure = {
                MediatorResult.Error(Exception(it.message))
            },
        )
    }
}
