package dev.avatsav.linkding.data.bookmarks

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dev.avatsav.linkding.data.model.Tag
import kotlinx.coroutines.flow.Flow

interface TagsRepository {
    fun getTagsPaged(
        pagingConfig: PagingConfig,
        selectedTags: List<Tag> = emptyList(),
    ): Flow<PagingData<Tag>>
}
