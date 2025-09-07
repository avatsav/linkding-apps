package dev.avatsav.linkding.bookmarks.ui.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.itemKey
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.overlay.LocalOverlayHost
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.AddBookmark
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.Delete
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.Open
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.Refresh
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.ToggleArchive
import dev.avatsav.linkding.bookmarks.ui.list.widgets.BookmarkListItem
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.inject.UserScope
import dev.avatsav.linkding.ui.BookmarksScreen
import kotlinx.coroutines.launch

@CircuitInject(BookmarksScreen::class, UserScope::class)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Bookmarks(state: BookmarksUiState, modifier: Modifier = Modifier) {
  val eventSink = state.eventSink
  val searchBarState = rememberSearchBarState()

  Scaffold(
    modifier = modifier,
    topBar = {
      SearchTopBar(
        searchBarState = searchBarState,
        searchState = state.search,
        eventSink = eventSink,
      )
    },
    floatingActionButton = {
      AnimatedVisibility(
        visible = searchBarState.currentValue == SearchBarValue.Collapsed,
        enter = scaleIn(),
        exit = scaleOut(),
      ) {
        FloatingActionButton(onClick = { eventSink(AddBookmark) }) {
          Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Bookmark")
        }
      }
    },
  ) { paddingValues ->
    val refreshing by
      rememberUpdatedState(state.bookmarkList.bookmarks.loadState.refresh == LoadState.Loading)
    val pullToRefreshState = rememberPullToRefreshState()
    PullToRefreshBox(
      modifier =
        Modifier.padding(
          top = paddingValues.calculateTopPadding(),
          start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
          end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
        ),
      isRefreshing = refreshing,
      onRefresh = { eventSink(Refresh) },
      state = pullToRefreshState,
      indicator = {
        PullToRefreshDefaults.LoadingIndicator(
          modifier = Modifier.align(Alignment.TopCenter),
          state = pullToRefreshState,
          isRefreshing = refreshing,
        )
      },
    ) {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = rememberLazyListState(),
        contentPadding = PaddingValues(bottom = 108.dp),
      ) {
        items(
          count = state.bookmarkList.bookmarks.itemCount,
          key = state.bookmarkList.bookmarks.itemKey { it.id },
        ) { index ->
          val bookmark = state.bookmarkList.bookmarks[index]
          if (bookmark != null) {
            BookmarkItem(
              bookmark = bookmark,
              onBookmarkOpen = { toOpen -> eventSink(Open(toOpen)) },
              onArchiveToggle = { toToggle -> eventSink(ToggleArchive(toToggle)) },
              onBookmarkDelete = { toDelete -> eventSink(Delete(toDelete)) },
            )
          }
        }
      }
    }
  }
}

@Composable
fun LazyItemScope.BookmarkItem(
  bookmark: Bookmark,
  onBookmarkOpen: (Bookmark) -> Unit,
  onArchiveToggle: (Bookmark) -> Unit,
  onBookmarkDelete: (Bookmark) -> Unit,
) {
  val scope = rememberCoroutineScope()
  val overlayHost = LocalOverlayHost.current
  BookmarkListItem(
    bookmark = bookmark,
    openBookmark = onBookmarkOpen,
    toggleArchive = { toToggle, dismissState ->
      scope.launch {
        when (overlayHost.showArchiveBookmarkAction(toToggle)) {
          ActionResult.Confirmed -> onArchiveToggle(toToggle)
          ActionResult.Cancelled,
          ActionResult.Dismissed -> dismissState.reset()
        }
      }
    },
    deleteBookmark = { toDelete, dismissState ->
      scope.launch {
        when (overlayHost.showDeleteBookmarkAction(toDelete)) {
          ActionResult.Confirmed -> onBookmarkDelete(toDelete)
          ActionResult.Cancelled,
          ActionResult.Dismissed -> dismissState.reset()
        }
      }
    },
    modifier = Modifier.animateItem(),
  )
}
