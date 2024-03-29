package dev.avatsav.linkding.data.bookmarks

import androidx.paging.ExperimentalPagingApi
import app.cash.paging.LoadType
import app.cash.paging.PagingState
import app.cash.paging.RemoteMediator
import app.cash.paging.RemoteMediatorMediatorResultError
import app.cash.paging.RemoteMediatorMediatorResultSuccess
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.mapEither
import dev.avatsav.linkding.AppCoroutineDispatchers
import dev.avatsav.linkding.Logger
import dev.avatsav.linkding.api.LinkdingApiProvider
import dev.avatsav.linkding.data.bookmarks.mappers.BookmarkErrorMapper
import dev.avatsav.linkding.data.bookmarks.mappers.BookmarkMapper
import dev.avatsav.linkding.data.bookmarks.mappers.toLinkding
import dev.avatsav.linkding.data.db.daos.PagingBookmarksDao
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.data.model.BookmarkCategory
import dev.avatsav.linkding.data.model.Tag
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

typealias BookmarksRemoteMediatorFactory = (BookmarkCategory, List<Tag>) -> BookmarksRemoteMediator

@OptIn(ExperimentalPagingApi::class)
@Inject
class BookmarksRemoteMediator(
    @Assisted private val category: BookmarkCategory,
    @Assisted private val selectedTags: List<Tag>,
    private val apiProvider: Lazy<LinkdingApiProvider>,
    private val bookmarksDao: PagingBookmarksDao,
    private val dispatchers: AppCoroutineDispatchers,
    private val bookmarkMapper: BookmarkMapper,
    private val errorMapper: BookmarkErrorMapper,
    private val logger: Logger,
) : RemoteMediator<Int, Bookmark>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Bookmark>,
    ): MediatorResult = withContext(dispatchers.io) {
        val offset = when (loadType) {
            LoadType.REFRESH -> 0
            LoadType.PREPEND -> return@withContext RemoteMediatorMediatorResultSuccess(
                endOfPaginationReached = true,
            )

            LoadType.APPEND -> {
                bookmarksDao.countBookmarks().toInt()
            }
        }
        val query = selectedTags.joinToString(separator = " ") { "#${it.name}" }

        apiProvider.value.bookmarksApi.getBookmarks(
            query = query,
            offset = offset,
            limit = state.config.pageSize,
            category = category.toLinkding(),
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
                RemoteMediatorMediatorResultSuccess(endOfPaginationReached = it.nextPage == null)
            },
            failure = {
                RemoteMediatorMediatorResultError(Exception(it.message))
            },
        )
    }
}
