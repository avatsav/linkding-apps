package dev.avatsav.linkding.domain.observers

import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import dev.avatsav.linkding.data.bookmarks.TagsRepository
import dev.avatsav.linkding.data.model.Tag
import dev.avatsav.linkding.domain.PagedObserver
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class ObserveTags(private val repository: TagsRepository) :
    PagedObserver<ObserveTags.Param, Tag>() {

    override fun createObservable(params: Param): Flow<PagingData<Tag>> {
        return repository.getTagsPaged(
            pagingConfig = params.pagingConfig,
            selectedTags = params.selectedTags,
        )
    }

    data class Param(
        val selectedTags: List<Tag> = emptyList(),
        override val pagingConfig: PagingConfig,
    ) : PagedObserver.Param<Tag>
}
