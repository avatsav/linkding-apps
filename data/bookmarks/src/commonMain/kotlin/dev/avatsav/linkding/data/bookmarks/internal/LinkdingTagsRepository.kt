package dev.avatsav.linkding.data.bookmarks.internal

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dev.avatsav.linkding.data.bookmarks.TagsRepository
import dev.avatsav.linkding.data.model.Tag
import dev.avatsav.linkding.inject.UserScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
@ContributesBinding(UserScope::class)
class LinkdingTagsRepository(private val pagingSource: TagsPagingSource) : TagsRepository {

  override fun getTagsPaged(
    pagingConfig: PagingConfig,
    selectedTags: List<Tag>,
  ): Flow<PagingData<Tag>> =
    Pager(config = pagingConfig, pagingSourceFactory = { pagingSource }).flow
}
