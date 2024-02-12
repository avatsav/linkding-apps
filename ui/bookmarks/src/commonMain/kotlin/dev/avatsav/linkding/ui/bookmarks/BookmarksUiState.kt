package dev.avatsav.linkding.ui.bookmarks

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Immutable
import app.cash.paging.compose.LazyPagingItems
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import dev.avatsav.linkding.data.model.Bookmark

@Immutable
@OptIn(ExperimentalMaterial3Api::class)
data class BookmarksUiState(
    val bookmarks: LazyPagingItems<Bookmark>,
    val pullToRefreshState: PullToRefreshState,
    val eventSink: (BookmarksUiEvent) -> Unit,
) : CircuitUiState

sealed interface BookmarksUiEvent : CircuitUiEvent {
    data class Archive(val bookmark: Bookmark) : BookmarksUiEvent
    data class Delete(val bookmark: Bookmark) : BookmarksUiEvent
    data class Open(val bookmark: Bookmark) : BookmarksUiEvent
    data object AddBookmark : BookmarksUiEvent
}
