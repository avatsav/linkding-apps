package dev.avatsav.linkding.data.bookmarks

import androidx.paging.ExperimentalPagingApi
import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import dev.avatsav.linkding.data.db.daos.PagingBookmarksDao
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.data.model.BookmarkCategory
import dev.avatsav.linkding.data.model.Tag
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class BookmarksPagingDataFactory(
    private val bookmarksRemoteMediatorFactory: BookmarksRemoteMediatorFactory,
    private val pagingBookmarksDao: PagingBookmarksDao,
) {

    @OptIn(ExperimentalPagingApi::class)
    operator fun invoke(
        pagingConfig: PagingConfig,
        category: BookmarkCategory = BookmarkCategory.All,
        selectedTags: List<Tag> = emptyList(),
    ): Flow<PagingData<Bookmark>> {
        return Pager(
            config = pagingConfig,
            remoteMediator = bookmarksRemoteMediatorFactory(category, selectedTags),
            pagingSourceFactory = { pagingBookmarksDao.offsetPagingSource() },
        ).flow
    }
}
