package dev.avatsav.linkding.domain.observers

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dev.avatsav.linkding.data.bookmarks.BookmarksRepository
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.data.model.BookmarkCategory
import dev.avatsav.linkding.data.model.Tag
import dev.avatsav.linkding.domain.PagedObserver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import me.tatarka.inject.annotations.Inject

@Inject
class ObserveSearchResults(private val repository: BookmarksRepository) :
    PagedObserver<ObserveSearchResults.Param, Bookmark>() {

    override fun createObservable(params: Param): Flow<PagingData<Bookmark>> {
        if (params.query.isBlank()) return flowOf(PagingData.empty())
        return repository.getBookmarksPaged(
            cached = false,
            pagingConfig = params.pagingConfig,
            query = params.query,
            category = params.category,
            tags = params.tags.map { it.name },
        )
    }

    data class Param(
        val query: String,
        val category: BookmarkCategory,
        val tags: List<Tag>,
        override val pagingConfig: PagingConfig,
    ) : PagedObserver.Param<Bookmark>
}
