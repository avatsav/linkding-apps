package dev.avatsav.linkding.data.bookmarks.internal

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dev.avatsav.linkding.data.bookmarks.TagsRepository
import dev.avatsav.linkding.data.model.Tag
import dev.avatsav.linkding.inject.UserScope
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

@Inject
@ContributesBinding(UserScope::class)
class LinkdingTagsRepository(private val pagingSourceFactory: TagsPagingSourceFactory) :
  TagsRepository {

  override fun getTagsPaged(
    pagingConfig: PagingConfig,
    selectedTags: List<Tag>,
  ): Flow<PagingData<Tag>> =
    Pager(config = pagingConfig, pagingSourceFactory = { pagingSourceFactory(selectedTags) }).flow
}
