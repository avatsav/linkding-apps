package dev.avatsav.linkding.ui.bookmarks

import androidx.compose.runtime.Immutable
import app.cash.paging.compose.LazyPagingItems
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import dev.avatsav.linkding.data.model.Bookmark

@Immutable
data class BookmarksUiState(
    val bookmarks: LazyPagingItems<Bookmark>,
    val eventSink: (BookmarksUiEvent) -> Unit,
) : CircuitUiState

sealed interface BookmarksUiEvent : CircuitUiEvent {
    data object Refresh : BookmarksUiEvent
    data class Archive(val bookmark: Bookmark) : BookmarksUiEvent
    data class Delete(val bookmark: Bookmark) : BookmarksUiEvent
    data class Open(val bookmark: Bookmark) : BookmarksUiEvent
    data object AddBookmark : BookmarksUiEvent
}
