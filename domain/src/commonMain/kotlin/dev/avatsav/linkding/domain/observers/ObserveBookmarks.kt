package dev.avatsav.linkding.domain.observers

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dev.avatsav.linkding.data.bookmarks.BookmarksRepository
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.data.model.BookmarkCategory
import dev.avatsav.linkding.data.model.Tag
import dev.avatsav.linkding.domain.PagedObserver
import me.tatarka.inject.annotations.Inject
import kotlinx.coroutines.flow.Flow

@Inject
class ObserveBookmarks(private val repository: BookmarksRepository) : PagedObserver<ObserveBookmarks.Param, Bookmark>() {

    override fun createObservable(params: Param): Flow<PagingData<Bookmark>> =
        repository.getBookmarksPaged(
            cached = params.cached,
            pagingConfig = params.pagingConfig,
            query = params.query,
            category = params.category,
            tags = params.tags.map { it.name },
        )

    data class Param(
        val cached: Boolean,
        val query: String,
        val category: BookmarkCategory,
        val tags: List<Tag>,
        override val pagingConfig: PagingConfig,
    ) : PagedObserver.Param<Bookmark>
}
