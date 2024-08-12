package dev.avatsav.linkding.ui.bookmarks

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Immutable
import androidx.paging.compose.LazyPagingItems
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.data.model.BookmarkCategory
import dev.avatsav.linkding.data.model.Tag

@Immutable
@OptIn(ExperimentalMaterial3Api::class)
data class BookmarksUiState(
    val bookmarkCategory: BookmarkCategory,
    val bookmarks: LazyPagingItems<Bookmark>,
    val searchResults: LazyPagingItems<Bookmark>,
    val selectedTags: List<Tag>,
    val isOnline: Boolean,
    val eventSink: (BookmarksUiEvent) -> Unit,
) : CircuitUiState

sealed interface BookmarksUiEvent : CircuitUiEvent {
    data object Refresh : BookmarksUiEvent
    data class ToggleArchive(val bookmark: Bookmark) : BookmarksUiEvent
    data class Delete(val bookmark: Bookmark) : BookmarksUiEvent
    data class Open(val bookmark: Bookmark) : BookmarksUiEvent
    data class SetBookmarkCategory(val bookmarkCategory: BookmarkCategory) : BookmarksUiEvent
    data object AddBookmark : BookmarksUiEvent
    data object ShowSettings : BookmarksUiEvent
    data class SelectTag(val tag: Tag) : BookmarksUiEvent
    data class RemoveTag(val tag: Tag) : BookmarksUiEvent
    data class Search(val query: String) : BookmarksUiEvent
    data object ClearSearch : BookmarksUiEvent
}
