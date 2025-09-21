package dev.avatsav.linkding.bookmarks.ui.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AppBarWithSearch
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.itemKey
import com.slack.circuit.overlay.ContentWithOverlays
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.Delete
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.Open
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.RemoveTag
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.SelectBookmarkCategory
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.SelectTag
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.ToggleArchive
import dev.avatsav.linkding.bookmarks.ui.list.widgets.BookmarkListItem
import dev.avatsav.linkding.bookmarks.ui.list.widgets.EmptySearchResults
import dev.avatsav.linkding.bookmarks.ui.list.widgets.FiltersBar
import dev.avatsav.linkding.bookmarks.ui.list.widgets.SearchHistoryHeader
import dev.avatsav.linkding.bookmarks.ui.list.widgets.SearchHistoryItem
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.ui.theme.Material3ShapeDefaults
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val SearchTextDebounce = 800L

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
  searchBarState: SearchBarState,
  searchState: SearchUiState,
  modifier: Modifier = Modifier,
  eventSink: (BookmarksUiEvent) -> Unit,
) {
  val textFieldState = rememberTextFieldState(searchState.query)
  val scope = rememberCoroutineScope()
  val scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior()

  LaunchedEffect(textFieldState.text) {
    delay(SearchTextDebounce)
    eventSink(BookmarksUiEvent.Search(textFieldState.text.toString()))
  }

  LaunchedEffect(searchBarState, eventSink) {
    snapshotFlow { searchBarState.currentValue }
      .collect { newValue ->
        if (newValue == SearchBarValue.Collapsed) {
          // Clear search when collapsing the search bar
          eventSink(BookmarksUiEvent.ClearSearch)
          textFieldState.clearText()
        }
      }
  }

  val inputField =
    @Composable {
      SearchBarDefaults.InputField(
        modifier = Modifier,
        searchBarState = searchBarState,
        textFieldState = textFieldState,
        onSearch = {
          scope.launch { eventSink(BookmarksUiEvent.Search(textFieldState.text.toString())) }
        },
        placeholder = { Text(text = "Search") },
        leadingIcon = {
          if (searchBarState.isExpanded()) {
            IconButton(onClick = { scope.launch { searchBarState.animateToCollapsed() } }) {
              Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
            }
          } else {
            Icon(Icons.Default.Search, contentDescription = null)
          }
        },
        trailingIcon = {
          if (searchBarState.isExpanded()) {
            // Only show close button when there's text to clear
            if (textFieldState.text.isNotEmpty()) {
              IconButton(onClick = { textFieldState.clearText() }) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
              }
            }
          } else {
            IconButton(onClick = { scope.launch { searchBarState.animateToExpanded() } }) {
              Icon(imageVector = Icons.Default.FilterList, contentDescription = "Filters")
            }
          }
        },
      )
    }

  AppBarWithSearch(
    modifier = modifier,
    scrollBehavior = scrollBehavior,
    state = searchBarState,
    inputField = inputField,
    actions = {
      IconButton(onClick = { eventSink(BookmarksUiEvent.ShowSettings) }) {
        Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
      }
    },
  )

  ExpandedFullScreenSearchBar(
    modifier = modifier,
    state = searchBarState,
    inputField = inputField,
  ) {
    Surface {
      SearchResultsContent(
        searchState = searchState,
        eventSink = eventSink,
        modifier = Modifier.fillMaxSize(),
      )
    }
  }
}

@Composable
private fun SearchResultsContent(
  searchState: SearchUiState,
  eventSink: (BookmarksUiEvent) -> Unit,
  modifier: Modifier = Modifier,
) {
  ContentWithOverlays {
    LazyColumn(modifier) {
      item(key = searchState.filters.bookmarkCategory) {
        FiltersBar(
          selectedCategory = searchState.filters.bookmarkCategory,
          onSelectCategory = { eventSink(SelectBookmarkCategory(it)) },
          selectedTags = searchState.filters.selectedTags,
          onSelectTag = { eventSink(SelectTag(it)) },
          onRemoveTag = { eventSink(RemoveTag(it)) },
          modifier = Modifier.fillMaxWidth().animateItem(),
        )
      }
      when {
        searchState.isIdle() -> SearchHistoryItems(searchState = searchState, eventSink = eventSink)
        searchState.isLoading() -> SearchResultsLoading()
        searchState.hasNoSearchResults() -> SearchResultsEmpty()
        else -> SearchResultItems(
          searchState = searchState,
          openBookmark = { bookmark -> eventSink(Open(bookmark)) },
        )
      }
    }
  }
}

private fun LazyListScope.SearchResultItems(
  searchState: SearchUiState,
  openBookmark: (Bookmark) -> Unit,
) {
  items(count = searchState.results.itemCount, key = searchState.results.itemKey { it.id }) { index
    ->
    val result = searchState.results[index]
    if (result != null) {
      BookmarkListItem(
        bookmark = result,
        selected = false,
        openBookmark = openBookmark,
        toggleActions = { /* Not supported in search results */ },
        modifier = Modifier.animateItem(),
      )
    }
  }
}

fun LazyListScope.SearchHistoryItems(
  searchState: SearchUiState,
  eventSink: (BookmarksUiEvent) -> Unit,
) {
  item(key = "history_header") {
    SearchHistoryHeader(
      onClearHistory = { eventSink(BookmarksUiEvent.ClearSearchHistory) },
      modifier = Modifier.animateItem(),
    )
  }
  items(
    count = searchState.history.size,
    key = { "history_${searchState.history[it].query}_${searchState.history[it].timestamp}" },
  ) { index ->
    val historyItem = searchState.history[index]
    SearchHistoryItem(
      searchHistory = historyItem,
      shape = Material3ShapeDefaults.itemShape(index, searchState.history.size),
      onClick = { eventSink(BookmarksUiEvent.SelectSearchHistoryItem(historyItem)) },
      modifier = Modifier.animateItem(),
    )
  }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
private fun LazyListScope.SearchResultsLoading() {
  item(key = "loading") {
    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
      ContainedLoadingIndicator()
    }
  }
}

private fun LazyListScope.SearchResultsEmpty() {
  item(key = "empty-results") { EmptySearchResults(modifier = Modifier.animateItem()) }
}

@OptIn(ExperimentalMaterial3Api::class)
fun SearchBarState.isCollapsed() = this.currentValue == SearchBarValue.Collapsed

@OptIn(ExperimentalMaterial3Api::class)
fun SearchBarState.isExpanded() = this.currentValue == SearchBarValue.Expanded
