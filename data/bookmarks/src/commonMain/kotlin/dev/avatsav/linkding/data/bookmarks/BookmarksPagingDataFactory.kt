package dev.avatsav.linkding.data.bookmarks

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dev.avatsav.linkding.data.db.daos.PagingBookmarksDao
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.data.model.BookmarkCategory
import dev.avatsav.linkding.inject.UserScope
import me.tatarka.inject.annotations.Inject
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalPagingApi::class)
@Inject
@UserScope
class BookmarksPagingDataFactory(
    private val bookmarksRemoteMediatorFactory: BookmarksRemoteMediatorFactory,
    private val remoteBookmarksPagingSourceFactory: RemoteBookmarksPagingSourceFactory,
    private val pagingBookmarksDao: PagingBookmarksDao,
) {
    fun create(
        cached: Boolean,
        pagingConfig: PagingConfig,
        param: Param,
    ): Flow<PagingData<Bookmark>> = if (cached) {
        cachedBookmarksFlow(pagingConfig, param)
    } else {
        remoteBookmarksFlow(pagingConfig, param)
    }

    private fun remoteBookmarksFlow(pagingConfig: PagingConfig, param: Param) = Pager(
        config = pagingConfig,
        pagingSourceFactory = {
            remoteBookmarksPagingSourceFactory(
                param.query,
                param.category,
                param.tags,
            )
        },
    ).flow

    private fun cachedBookmarksFlow(pagingConfig: PagingConfig, param: Param) = Pager(
        config = pagingConfig,
        remoteMediator = bookmarksRemoteMediatorFactory(
            param.query,
            param.category,
            param.tags,
        ),
        pagingSourceFactory = { pagingBookmarksDao.bookmarksPagingSource() },
    ).flow

    data class Param(
        val query: String = "",
        val category: BookmarkCategory = BookmarkCategory.All,
        val tags: List<String> = emptyList(),
    )
}
