package dev.avatsav.linkding.bookmarks.ui.tags

import androidx.compose.runtime.Immutable
import androidx.paging.compose.LazyPagingItems
import dev.avatsav.linkding.data.model.Tag

@Immutable data class TagsUiState(val selectedTags: List<Tag>, val tags: LazyPagingItems<Tag>)

sealed interface TagsUiEvent {
  data class SelectTag(val tag: Tag) : TagsUiEvent

  data object Close : TagsUiEvent
}

sealed interface TagsEffect {
  data class TagSelected(val tag: Tag) : TagsEffect

  data object Dismiss : TagsEffect
}
