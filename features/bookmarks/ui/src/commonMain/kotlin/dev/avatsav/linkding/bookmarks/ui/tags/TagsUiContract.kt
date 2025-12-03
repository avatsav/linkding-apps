package dev.avatsav.linkding.bookmarks.ui.tags

import androidx.compose.runtime.Immutable
import androidx.paging.compose.LazyPagingItems
import dev.avatsav.linkding.data.model.Tag

/** Result wrapper for tag selection to avoid type erasure issues with generic List<Tag>. */
@Immutable data class TagsSelectionResult(val selectedTags: List<Tag>)

@Immutable data class TagsUiState(val selectedTags: List<Tag>, val tags: LazyPagingItems<Tag>)

sealed interface TagsUiEvent {
  data class ToggleTag(val tag: Tag) : TagsUiEvent

  data object Confirm : TagsUiEvent

  data object Close : TagsUiEvent
}

sealed interface TagsUiEffect {
  data class TagsConfirmed(val selectedTags: List<Tag>) : TagsUiEffect

  data object Dismiss : TagsUiEffect
}
