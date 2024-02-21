package dev.avatsav.linkding.domain.observers

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingConfig
import app.cash.paging.LoadType
import app.cash.paging.Pager
import app.cash.paging.PagingData
import app.cash.paging.PagingState
import app.cash.paging.RemoteMediator
import app.cash.paging.RemoteMediatorMediatorResultError
import app.cash.paging.RemoteMediatorMediatorResultSuccess
import com.github.michaelbull.result.fold
import dev.avatsav.linkding.AppCoroutineDispatchers
import dev.avatsav.linkding.Logger
import dev.avatsav.linkding.data.bookmarks.BookmarksRepository
import dev.avatsav.linkding.data.db.daos.PagingBookmarksDao
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.domain.PagedObserver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@OptIn(ExperimentalPagingApi::class)
@Inject
class ObservePagedBookmarks(
    private val pagingBookmarksDao: PagingBookmarksDao,
    private val bookmarksRemoteMediator: BookmarksRemoteMediator,
) : PagedObserver<ObservePagedBookmarks.Param, Bookmark>() {

    override fun createObservable(params: Param): Flow<PagingData<Bookmark>> {
        return Pager(
            config = params.pagingConfig,
            remoteMediator = bookmarksRemoteMediator,
            pagingSourceFactory = { pagingBookmarksDao.keyedPagingSource() },
        ).flow
    }

    data class Param(override val pagingConfig: PagingConfig) : PagedObserver.Param<Bookmark>
}

@OptIn(ExperimentalPagingApi::class)
@Inject
class BookmarksRemoteMediator(
    private val repository: BookmarksRepository,
    private val bookmarksDao: PagingBookmarksDao,
    private val dispatchers: AppCoroutineDispatchers,
    private val logger: Logger,
) : RemoteMediator<Int, Bookmark>() {

    override suspend fun initialize(): InitializeAction {
        return super.initialize()
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Bookmark>,
    ): MediatorResult = withContext(dispatchers.io) {
        logger.w { "Remote Mediation, LoadType:$loadType, PagingState:$state" }
        val offset = when (loadType) {
            LoadType.REFRESH -> 0
            LoadType.PREPEND -> return@withContext RemoteMediatorMediatorResultSuccess(
                endOfPaginationReached = true,
            )

            LoadType.APPEND -> {
                logger.w { "AnchorPosition:${state.anchorPosition}" }
                state.anchorPosition ?: 0
            }
        }
        return@withContext repository.getBookmarks(offset, 20).fold(
            success = {
                val bookmarks = it.bookmarks
                if (loadType == androidx.paging.LoadType.REFRESH) {
                    logger.w { "refreshing the database" }
                    bookmarksDao.refresh(bookmarks)
                } else {
                    logger.w { "appending entries in the database" }
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
