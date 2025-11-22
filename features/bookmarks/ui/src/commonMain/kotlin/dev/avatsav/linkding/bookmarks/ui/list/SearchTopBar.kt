package dev.avatsav.linkding.bookmarks.ui.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material3.FloatingToolbarDefaults.ScreenOffset
import androidx.compose.material3.FloatingToolbarDefaults.floatingToolbarVerticalNestedScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.paging.compose.itemKey
import dev.avatsav.linkding.bookmarks.ui.list.search.BookmarkSearchUiState
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
@Suppress("ModifierMissing")
fun SearchTopBar(
  searchBarState: SearchBarState,
  searchState: BookmarkSearchUiState,
  onShowSettings: () -> Unit,
  eventSink: (BookmarkSearchUiEvent) -> Unit,
) {
  val textFieldState = rememberTextFieldState(searchState.query)
  val scope = rememberCoroutineScope()

  // Sync text field with external query changes (e.g., from search history)
  LaunchedEffect(searchState.query) {
    if (textFieldState.text.toString() != searchState.query) {
      textFieldState.edit {
        replace(0, length, searchState.query)
        selection = TextRange(searchState.query.length)
      }
    }
  }

  // Debounce search query
  LaunchedEffect(textFieldState.text) {
    val searchQuery = textFieldState.text.toString()

    // don't debounce if the search query is empty
    if (searchQuery.isNotBlank()) {
      delay(SearchTextDebounce)
    }
    if (searchQuery != searchState.query) {
      eventSink(BookmarkSearchUiEvent.Search(searchQuery))
    }
  }

  LaunchedEffect(searchBarState) {
    snapshotFlow { searchBarState.currentValue }
      .collect { newValue ->
        if (newValue == SearchBarValue.Collapsed) {
          // Clear search when collapsing the search bar
          eventSink(BookmarkSearchUiEvent.ClearSearch)
          textFieldState.clearText()
        }
      }
  }

  val inputField =
    @Composable {
      SearchBarDefaults.InputField(
        searchBarState = searchBarState,
        textFieldState = textFieldState,
        onSearch = {
          scope.launch { eventSink(BookmarkSearchUiEvent.Search(textFieldState.text.toString())) }
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
    state = searchBarState,
    inputField = inputField,
    actions = {
      IconButton(onClick = onShowSettings) {
        Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
      }
    },
    colors = SearchBarDefaults.appBarWithSearchColors(),
  )

  ExpandedFullScreenSearchBar(state = searchBarState, inputField = inputField) {
    SearchResultsContent(
      searchState = searchState,
      eventSink = eventSink,
      modifier = Modifier.fillMaxSize(),
    )
  }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SearchResultsContent(
  searchState: BookmarkSearchUiState,
  eventSink: (BookmarkSearchUiEvent) -> Unit,
  modifier: Modifier = Modifier,
) {
  val actionableBookmark = remember { mutableStateOf<Bookmark?>(null) }
  val snackbarHostState = remember { SnackbarHostState() }

  // Handle snackbar messages
  LaunchedEffect(searchState.snackbarMessage) {
    searchState.snackbarMessage?.let { message ->
      val result =
        snackbarHostState.showSnackbar(
          message = message.message,
          actionLabel = message.actionLabel,
          duration = SnackbarDuration.Short,
        )

      when (result) {
        SnackbarResult.ActionPerformed -> {
          message.onAction?.invoke()
        }
        SnackbarResult.Dismissed -> {
          eventSink(BookmarkSearchUiEvent.DismissSnackbar)
        }
      }
    }
  }

  Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }, modifier = modifier) { paddingValues
    ->
    Box(Modifier.fillMaxSize()) {
      ActionableBookmarkToolbar(
        actionableBookmark = actionableBookmark.value,
        onDismiss = { actionableBookmark.value = null },
        editBookmark = { eventSink(BookmarkSearchUiEvent.Edit(it)) },
        toggleArchive = { eventSink(BookmarkSearchUiEvent.ToggleArchive(it)) },
        deleteBookmark = { eventSink(BookmarkSearchUiEvent.Delete(it)) },
        modifier =
          Modifier.align(Alignment.BottomCenter)
            .navigationBarsPadding()
            .offset(y = -ScreenOffset)
            .zIndex(1f),
      )
      LazyColumn(
        Modifier.floatingToolbarVerticalNestedScroll(
          expanded = actionableBookmark.value != null,
          onExpand = { actionableBookmark.value = null },
          onCollapse = { actionableBookmark.value = null },
        )
      ) {
        item(key = searchState.filters.bookmarkCategory) {
          FiltersBar(
            selectedCategory = searchState.filters.bookmarkCategory,
            onSelectCategory = { eventSink(BookmarkSearchUiEvent.SelectBookmarkCategory(it)) },
            selectedTags = searchState.filters.selectedTags,
            onSelectTag = { eventSink(BookmarkSearchUiEvent.SelectTag(it)) },
            onRemoveTag = { eventSink(BookmarkSearchUiEvent.RemoveTag(it)) },
            modifier = Modifier.fillMaxWidth().animateItem(),
          )
        }
        when {
          searchState.isIdle() ->
            searchHistoryItems(searchState = searchState, eventSink = eventSink)

          searchState.isLoading() -> searchResultsLoading()
          searchState.hasNoSearchResults() -> searchResultsEmpty()
          else ->
            searchResultItems(
              searchState = searchState,
              isSelected = { bookmark -> actionableBookmark.value == bookmark },
              openBookmark = { bookmark -> eventSink(BookmarkSearchUiEvent.Open(bookmark)) },
              toggleActions = { bookmark -> actionableBookmark.value = bookmark },
            )
        }
      }
    }
  }
}

private fun LazyListScope.searchResultItems(
  searchState: BookmarkSearchUiState,
  isSelected: (Bookmark) -> Boolean,
  openBookmark: (Bookmark) -> Unit,
  toggleActions: (Bookmark) -> Unit,
) {
  items(count = searchState.results.itemCount, key = searchState.results.itemKey { it.id }) { index
    ->
    val result = searchState.results[index]
    if (result != null) {
      BookmarkListItem(
        bookmark = result,
        selected = isSelected(result),
        openBookmark = openBookmark,
        toggleActions = toggleActions,
        modifier = Modifier.animateItem(),
      )
    }
  }
}

fun LazyListScope.searchHistoryItems(
  searchState: BookmarkSearchUiState,
  eventSink: (BookmarkSearchUiEvent) -> Unit,
) {
  item(key = "history_header") {
    SearchHistoryHeader(
      onClearHistory = { eventSink(BookmarkSearchUiEvent.ClearSearchHistory) },
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
      onClick = { eventSink(BookmarkSearchUiEvent.SelectSearchHistoryItem(historyItem)) },
      modifier = Modifier.animateItem(),
      shape = Material3ShapeDefaults.itemShape(index, searchState.history.size),
    )
  }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
private fun LazyListScope.searchResultsLoading() {
  item(key = "loading") {
    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
      ContainedLoadingIndicator()
    }
  }
}

private fun LazyListScope.searchResultsEmpty() {
  item(key = "empty-results") { EmptySearchResults(modifier = Modifier.animateItem()) }
}

@OptIn(ExperimentalMaterial3Api::class)
fun SearchBarState.isCollapsed() = this.currentValue == SearchBarValue.Collapsed

@OptIn(ExperimentalMaterial3Api::class)
fun SearchBarState.isExpanded() = this.currentValue == SearchBarValue.Expanded
