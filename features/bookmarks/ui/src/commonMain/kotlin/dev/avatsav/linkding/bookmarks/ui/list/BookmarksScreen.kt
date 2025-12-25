package dev.avatsav.linkding.bookmarks.ui.list

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Unarchive
import androidx.compose.material3.AppBarWithSearch
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarDefaults.ScreenOffset
import androidx.compose.material3.FloatingToolbarDefaults.floatingToolbarVerticalNestedScroll
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import androidx.paging.LoadState
import androidx.paging.compose.itemKey
import dev.avatsav.linkding.bookmarks.ui.list.widgets.BookmarkListItem
import dev.avatsav.linkding.bookmarks.ui.list.widgets.EmptySearchResults
import dev.avatsav.linkding.bookmarks.ui.list.widgets.FiltersBar
import dev.avatsav.linkding.bookmarks.ui.list.widgets.SearchHistoryHeader
import dev.avatsav.linkding.bookmarks.ui.list.widgets.SearchHistoryItem
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.data.model.SearchHistory
import dev.avatsav.linkding.navigation.LocalNavigator
import dev.avatsav.linkding.navigation.Route
import dev.avatsav.linkding.navigation.RouteNavigator
import dev.avatsav.linkding.navigation.rememberResultNavigator
import dev.avatsav.linkding.ui.compose.appearFromBottom
import dev.avatsav.linkding.ui.compose.disappearToBottom
import dev.avatsav.linkding.ui.compose.widgets.AnimatedVisibilityWithElevation
import dev.avatsav.linkding.ui.theme.Material3ShapeDefaults
import dev.avatsav.linkding.viewmodel.ObserveEffects
import kotlinx.coroutines.launch

@OptIn(
  ExperimentalMaterial3Api::class,
  ExperimentalMaterial3ExpressiveApi::class,
  ExperimentalComposeUiApi::class,
)
@Composable
fun BookmarksScreen(viewModel: BookmarksViewModel, modifier: Modifier = Modifier) {
  val navigator = LocalNavigator.current
  val state by viewModel.models.collectAsStateWithLifecycle()
  val eventSink = viewModel::eventSink

  val tagsNavigator =
    rememberResultNavigator<Route.Tags, Route.Tags.Result> { result ->
      when (result) {
        is Route.Tags.Result.Confirmed -> {
          viewModel.eventSink(BookmarksUiEvent.SetTags(result.selectedTags))
        }
        Route.Tags.Result.Dismissed -> {
          // No action needed on dismissal
        }
      }
    }

  ObserveEffects(viewModel.effects) { effect ->
    when (effect) {
      BookmarkUiEffect.AddBookmark -> navigator.goTo(Route.AddBookmark.New)
      is BookmarkUiEffect.EditBookmark -> navigator.goTo(Route.AddBookmark.Edit(effect.bookmark.id))
      BookmarkUiEffect.NavigateToSettings -> navigator.goTo(Route.Settings)
      is BookmarkUiEffect.OpenBookmark -> navigator.goTo(Route.Url(effect.bookmark.url))
    }
  }

  BookmarksScreen(state, tagsNavigator, modifier, eventSink)
}

@OptIn(
  ExperimentalMaterial3Api::class,
  ExperimentalMaterial3ExpressiveApi::class,
  ExperimentalComposeUiApi::class,
)
@Composable
private fun BookmarksScreen(
  state: BookmarksUiState,
  tagsNavigator: RouteNavigator<Route.Tags>,
  modifier: Modifier = Modifier,
  eventSink: (BookmarksUiEvent) -> Unit,
) {
  val scope = rememberCoroutineScope()
  val currentEventSink by rememberUpdatedState(eventSink)
  val actionableBookmark = remember { mutableStateOf<Bookmark?>(null) }
  val snackbarHostState = remember { SnackbarHostState() }
  val searchBarState = rememberSearchBarState()
  val searchTextFieldState = rememberTextFieldState()
  val scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior()

  NavigationBackHandler(
    state = rememberNavigationEventState(NavigationEventInfo.None),
    isBackEnabled = actionableBookmark.value != null,
    onBackCompleted = { actionableBookmark.value = null },
  )

  // Handle snackbar messages
  LaunchedEffect(state.snackbarMessage) {
    state.snackbarMessage?.let { message ->
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
          currentEventSink(BookmarksUiEvent.DismissSnackbar)
        }
      }
    }
  }

  val searchInputField =
    @Composable {
      SearchBarDefaults.InputField(
        searchBarState = searchBarState,
        textFieldState = searchTextFieldState,
        onSearch = {
          scope.launch { searchBarState.animateToCollapsed() }
          currentEventSink(BookmarksUiEvent.Search(searchTextFieldState.text.toString()))
        },
        placeholder = {
          if (searchTextFieldState.text.isEmpty()) {
            Text("Search")
          } else {
            Text("${searchTextFieldState.text}")
          }
        },
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
          if (searchTextFieldState.text.isNotEmpty()) {
            IconButton(
              onClick = {
                searchTextFieldState.clearText()
                currentEventSink(BookmarksUiEvent.ClearSearch)
              }
            ) {
              Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
            }
          }
        },
      )
    }

  Scaffold(
    modifier = modifier,
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
      AppBarWithSearch(
        state = searchBarState,
        inputField = searchInputField,
        actions = {
          IconButton(onClick = { currentEventSink(BookmarksUiEvent.ShowSettings) }) {
            Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
          }
        },
        scrollBehavior = scrollBehavior,
        colors =
          SearchBarDefaults.appBarWithSearchColors(
            scrolledAppBarContainerColor = MaterialTheme.colorScheme.surface,
            scrolledSearchBarContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
          ),
      )

      ExpandedFullScreenSearchBar(state = searchBarState, inputField = searchInputField) {
        Surface(modifier = Modifier.fillMaxSize()) {
          LazyColumn {
            if (state.searchHistory.isNotEmpty())
              searchHistoryItems(
                items = state.searchHistory,
                onClearHistory = { currentEventSink(BookmarksUiEvent.ClearSearchHistory) },
                onSelectHistory = {
                  scope.launch { searchBarState.animateToCollapsed() }
                  searchTextFieldState.setTextAndPlaceCursorAtEnd(it.query)
                  currentEventSink(BookmarksUiEvent.SelectSearchHistory(it))
                },
              )
            else searchResultsEmpty()
          }
        }
      }
    },
    floatingActionButton = {
      AnimatedVisibilityWithElevation(
        visible = actionableBookmark.value == null,
        enter = appearFromBottom(),
        exit = disappearToBottom(),
      ) { elevation ->
        FloatingActionButton(
          onClick = { currentEventSink(BookmarksUiEvent.AddBookmark) },
          elevation = FloatingActionButtonDefaults.elevation(defaultElevation = elevation),
        ) {
          Icon(imageVector = Icons.Filled.Add, contentDescription = "Add bookmark")
        }
      }
    },
  ) { paddingValues ->
    val refreshing by rememberUpdatedState(state.bookmarks.loadState.refresh == LoadState.Loading)
    val pullToRefreshState = rememberPullToRefreshState()
    PullToRefreshBox(
      modifier =
        Modifier.fillMaxSize()
          .padding(
            top = paddingValues.calculateTopPadding(),
            start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
            end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
          ),
      isRefreshing = refreshing,
      onRefresh = { currentEventSink(BookmarksUiEvent.Refresh) },
      state = pullToRefreshState,
      indicator = {
        PullToRefreshDefaults.LoadingIndicator(
          modifier = Modifier.align(Alignment.TopCenter),
          state = pullToRefreshState,
          isRefreshing = refreshing,
        )
      },
    ) {
      ActionableBookmarkToolbar(
        actionableBookmark = actionableBookmark.value,
        onDismiss = { actionableBookmark.value = null },
        editBookmark = { currentEventSink(BookmarksUiEvent.Edit(it)) },
        toggleArchive = { currentEventSink(BookmarksUiEvent.ToggleArchive(it)) },
        deleteBookmark = { currentEventSink(BookmarksUiEvent.Delete(it)) },
        modifier =
          Modifier.align(Alignment.BottomCenter)
            .navigationBarsPadding()
            .offset(y = -ScreenOffset)
            .zIndex(1f),
      )

      LazyColumn(
        modifier =
          Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
            .floatingToolbarVerticalNestedScroll(
              expanded = actionableBookmark.value != null,
              onExpand = { actionableBookmark.value = null },
              onCollapse = { actionableBookmark.value = null },
            ),
        state = rememberLazyListState(),
        contentPadding = PaddingValues(bottom = 108.dp),
      ) {
        // Filters bar as first item
        item(key = "filters_bar") {
          FiltersBar(
            selectedCategory = state.category,
            onSelectCategory = { currentEventSink(BookmarksUiEvent.SelectCategory(it)) },
            selectedTags = state.selectedTags,
            onOpenTagSelector = {
              tagsNavigator(Route.Tags(state.selectedTags.map { it.id }.toSet()))
            },
            onRemoveTag = { currentEventSink(BookmarksUiEvent.RemoveTag(it)) },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).animateItem(),
          )
        }

        // Bookmark items
        items(count = state.bookmarks.itemCount, key = state.bookmarks.itemKey { it.id }) { index ->
          val bookmark = state.bookmarks[index]
          if (bookmark != null) {
            BookmarkListItem(
              modifier = Modifier.animateItem(),
              bookmark = bookmark,
              selected = actionableBookmark.value == bookmark,
              openBookmark = { toOpen ->
                if (actionableBookmark.value == toOpen) {
                  actionableBookmark.value = null
                } else {
                  actionableBookmark.value = null
                  currentEventSink(BookmarksUiEvent.Open(toOpen))
                }
              },
              toggleActions = { bookmark -> actionableBookmark.value = bookmark },
            )
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun ActionableBookmarkToolbar(
  actionableBookmark: Bookmark?,
  onDismiss: () -> Unit,
  editBookmark: (Bookmark) -> Unit,
  toggleArchive: (Bookmark) -> Unit,
  deleteBookmark: (Bookmark) -> Unit,
  modifier: Modifier = Modifier,
) {
  AnimatedVisibilityWithElevation(
    visible = actionableBookmark != null,
    modifier = modifier,
    enter = appearFromBottom(),
    exit = disappearToBottom(),
  ) { elevation ->
    HorizontalFloatingToolbar(
      expanded = true,
      colors = FloatingToolbarDefaults.vibrantFloatingToolbarColors(),
      expandedShadowElevation = elevation,
      content = {
        IconButton(
          onClick = {
            val bookmark = actionableBookmark ?: return@IconButton
            onDismiss()
            toggleArchive(bookmark)
          }
        ) {
          val bookmark = actionableBookmark ?: return@IconButton
          if (bookmark.archived) {
            Icon(Icons.Filled.Unarchive, contentDescription = "Unarchive bookmark")
          } else {
            Icon(Icons.Filled.Archive, contentDescription = "Archive bookmark")
          }
        }
        IconButton(
          onClick = {
            val bookmark = actionableBookmark ?: return@IconButton
            onDismiss()
            deleteBookmark(bookmark)
          }
        ) {
          Icon(Icons.Filled.Delete, contentDescription = "Delete bookmark")
        }
        IconButton(
          onClick = {
            val bookmark = actionableBookmark ?: return@IconButton
            onDismiss()
            editBookmark(bookmark)
          }
        ) {
          Icon(Icons.Filled.Edit, contentDescription = "Edit bookmark")
        }
      },
    )
  }
}

private fun LazyListScope.searchHistoryItems(
  items: List<SearchHistory>,
  onClearHistory: () -> Unit = {},
  onSelectHistory: (SearchHistory) -> Unit,
) {

  item(key = "history_header") {
    SearchHistoryHeader(onClearHistory = onClearHistory, modifier = Modifier.animateItem())
  }
  items(count = items.size, key = { "history_${items[it].query}_${items[it].modified}" }) { index ->
    val historyItem = items[index]
    SearchHistoryItem(
      searchHistory = historyItem,
      onClick = { onSelectHistory(historyItem) },
      modifier = Modifier.animateItem(),
      shape = Material3ShapeDefaults.itemShape(index, items.size),
    )
  }
}

private fun LazyListScope.searchResultsEmpty() {
  item(key = "empty-results") { EmptySearchResults(modifier = Modifier.animateItem()) }
}

@OptIn(ExperimentalMaterial3Api::class)
private fun SearchBarState.isExpanded() = this.currentValue == SearchBarValue.Expanded
