package dev.avatsav.linkding.domain.observers

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dev.avatsav.linkding.data.bookmarks.TagsRepository
import dev.avatsav.linkding.data.model.Tag
import dev.avatsav.linkding.domain.PagedObserver
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
class ObserveTags(private val repository: TagsRepository) :
  PagedObserver<ObserveTags.Param, Tag>() {

  override fun createObservable(params: Param): Flow<PagingData<Tag>> =
    repository.getTagsPaged(pagingConfig = params.pagingConfig, selectedTags = params.selectedTags)

  data class Param(
    val selectedTags: List<Tag> = emptyList(),
    override val pagingConfig: PagingConfig,
  ) : PagedObserver.Param<Tag>
}
