package dev.avatsav.linkding.data.bookmarks

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dev.avatsav.linkding.data.model.Tag
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class TagsRepository(private val pagingSourceFactory: TagsPagingSourceFactory) {

    fun getTagsPaged(
        pagingConfig: PagingConfig,
        selectedTags: List<Tag> = emptyList(),
    ): Flow<PagingData<Tag>> {
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { pagingSourceFactory(selectedTags) },
        ).flow
    }
}
