package dev.avatsav.linkding.data.bookmarks

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dev.avatsav.linkding.data.model.Tag
import me.tatarka.inject.annotations.Inject
import kotlinx.coroutines.flow.Flow

@Inject
class TagsRepository(private val pagingSourceFactory: TagsPagingSourceFactory) {

    fun getTagsPaged(
        pagingConfig: PagingConfig,
        selectedTags: List<Tag> = emptyList(),
    ): Flow<PagingData<Tag>> = Pager(
        config = pagingConfig,
        pagingSourceFactory = { pagingSourceFactory(selectedTags) },
    ).flow
}
