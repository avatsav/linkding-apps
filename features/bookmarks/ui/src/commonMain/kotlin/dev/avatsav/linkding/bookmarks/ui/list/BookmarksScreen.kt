package dev.avatsav.linkding.bookmarks.ui.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.overlay.ContentWithOverlays
import com.slack.circuit.overlay.LocalOverlayHost
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.AddBookmark
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.ClearSearch
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.Delete
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.Open
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.Refresh
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.RemoveTag
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.Search
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.SelectTag
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.SetBookmarkCategory
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.ShowSettings
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.ToggleArchive
import dev.avatsav.linkding.bookmarks.ui.list.widgets.BookmarkListItem
import dev.avatsav.linkding.bookmarks.ui.list.widgets.FiltersBar
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.inject.UserScope
import dev.avatsav.linkding.ui.BookmarksScreen
import kotlinx.coroutines.launch

@CircuitInject(BookmarksScreen::class, UserScope::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Bookmarks(state: BookmarksUiState, modifier: Modifier = Modifier) {
  val eventSink = state.eventSink
  var searchQuery by remember { mutableStateOf("") }
  var searchActive by rememberSaveable { mutableStateOf(false) }
  val listState = rememberLazyListState()
  val scrolledToTop by remember {
    derivedStateOf {
      listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
    }
  }
  val searchTopAppBarState = rememberSearchTopAppBarState(searchActive, scrolledToTop)
  LaunchedEffect(searchTopAppBarState) {
    if (!searchActive) {
      searchQuery = ""
      eventSink(ClearSearch)
    }
  }

  Scaffold(
    modifier = modifier,
    topBar = {
      SearchTopBar(
        searchQuery = searchQuery,
        searchQueryChange = { searchQuery = it },
        expanded = searchActive,
        onExpandedChange = { searchActive = it },
        state = searchTopAppBarState,
        searchResults = state.searchResults,
        isOnline = state.isOnline,
        eventSink = eventSink,
      )
    },
    floatingActionButton = {
      AnimatedVisibility(visible = !searchActive, enter = scaleIn(), exit = scaleOut()) {
        FloatingActionButton(onClick = { eventSink(AddBookmark) }) {
          Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Bookmark")
        }
      }
    },
  ) { paddingValues ->
    val refreshing by rememberUpdatedState(state.bookmarks.loadState.refresh == LoadState.Loading)

    PullToRefreshBox(
      modifier =
        Modifier.padding(
          top = paddingValues.calculateTopPadding(),
          start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
          end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
        ),
      isRefreshing = refreshing,
      onRefresh = { eventSink(Refresh) },
    ) {
      ContentWithOverlays {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          state = listState,
          contentPadding = PaddingValues(bottom = 108.dp),
        ) {
          item(key = state.bookmarkCategory) {
            FiltersBar(
              selectedCategory = state.bookmarkCategory,
              onSelectCategory = { eventSink(SetBookmarkCategory(it)) },
              selectedTags = state.selectedTags.toList(),
              onSelectTag = { eventSink(SelectTag(it)) },
              onRemoveTag = { eventSink(RemoveTag(it)) },
              modifier = Modifier.fillMaxWidth().animateItem(),
            )
          }
          items(count = state.bookmarks.itemCount, key = state.bookmarks.itemKey { it.id }) { index
            ->
            val bookmark = state.bookmarks[index]
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
}

@Composable
private fun LazyItemScope.BookmarkItem(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchTopBar(
  searchQuery: String,
  searchQueryChange: (String) -> Unit,
  state: SearchTopAppBarState,
  searchResults: LazyPagingItems<Bookmark>,
  expanded: Boolean,
  isOnline: Boolean,
  onExpandedChange: (Boolean) -> Unit,
  modifier: Modifier = Modifier,
  eventSink: (BookmarksUiEvent) -> Unit,
) {
  val focusManager = LocalFocusManager.current

  Box(
    modifier =
      modifier.background(
        MaterialTheme.colorScheme.surfaceColorAtElevation(state.searchBarBgSurfaceElevation)
      )
  ) {
    SearchBar(
      modifier =
        Modifier.fillMaxWidth()
          .padding(horizontal = state.searchBarHorizontalPadding)
          .padding(bottom = state.searchBarBottomPadding),
      inputField = {
        SearchBarDefaults.InputField(
          query = searchQuery,
          onQueryChange = searchQueryChange,
          onSearch = {
            focusManager.clearFocus()
            eventSink(Search(searchQuery))
          },
          expanded = expanded,
          onExpandedChange = onExpandedChange,
          enabled = true,
          placeholder = { Text("Search for words or #tags") },
          leadingIcon = {
            Icon(
              imageVector = if (isOnline) Icons.Default.Search else Icons.Default.CloudOff,
              contentDescription = "Search Icon",
            )
          },
          trailingIcon = {
            IconButton(
              onClick = {
                if (expanded) onExpandedChange(expanded.not()) else eventSink(ShowSettings)
              }
            ) {
              Icon(
                imageVector = if (expanded) Icons.Default.Close else Icons.Outlined.Settings,
                contentDescription = if (expanded) "Close" else "Settings",
              )
            }
          },
          interactionSource = null,
        )
      },
      expanded = expanded,
      onExpandedChange = onExpandedChange,
      colors = SearchBarDefaults.colors(containerColor = state.searchBarContainerColor),
      shape = SearchBarDefaults.inputFieldShape,
      shadowElevation = SearchBarDefaults.ShadowElevation,
      windowInsets = SearchBarDefaults.windowInsets,
    ) {
      LazyColumn {
        items(count = searchResults.itemCount, key = searchResults.itemKey { it.id }) { index ->
          val result = searchResults[index]
          if (result != null) {
            BookmarkItem(
              bookmark = result,
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
private fun rememberSearchTopAppBarState(
  searchActive: Boolean,
  scrolledToTop: Boolean,
): SearchTopAppBarState {
  val searchBarHorizontalPadding: Dp by animateDpAsState(if (searchActive) 0.dp else 12.dp)
  val searchBarBottomPadding: Dp by animateDpAsState(if (searchActive) 0.dp else 12.dp)
  val searchBarBgSurfaceElevation: Dp by animateDpAsState(if (scrolledToTop) 0.dp else 8.dp)
  val searchBarContainerColor by
    animateColorAsState(
      if (scrolledToTop && !searchActive) {
        MaterialTheme.colorScheme.surfaceVariant
      } else {
        MaterialTheme.colorScheme.surface
      }
    )
  return SearchTopAppBarState(
    searchBarHorizontalPadding = searchBarHorizontalPadding,
    searchBarBottomPadding = searchBarBottomPadding,
    searchBarBgSurfaceElevation = searchBarBgSurfaceElevation,
    searchBarContainerColor = searchBarContainerColor,
  )
}

@Immutable
private data class SearchTopAppBarState(
  val searchBarHorizontalPadding: Dp,
  val searchBarBottomPadding: Dp,
  val searchBarBgSurfaceElevation: Dp,
  val searchBarContainerColor: Color,
)
