package dev.avatsav.linkding.bookmarks.ui.list

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.material3.FloatingToolbarDefaults.ScreenOffset
import androidx.compose.material3.FloatingToolbarDefaults.floatingToolbarVerticalNestedScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
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
import dev.avatsav.linkding.bookmarks.ui.list.widgets.BookmarkActionsToolbar
import dev.avatsav.linkding.bookmarks.ui.list.widgets.BookmarkListItem
import dev.avatsav.linkding.bookmarks.ui.list.widgets.EmptySearchResults
import dev.avatsav.linkding.bookmarks.ui.list.widgets.FiltersBar
import dev.avatsav.linkding.bookmarks.ui.list.widgets.PendingBookmarkActions
import dev.avatsav.linkding.bookmarks.ui.list.widgets.PendingBookmarkListItem
import dev.avatsav.linkding.bookmarks.ui.list.widgets.PendingBookmarksHeader
import dev.avatsav.linkding.bookmarks.ui.list.widgets.SearchHistoryHeader
import dev.avatsav.linkding.bookmarks.ui.list.widgets.SearchHistoryItem
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.data.model.PendingBookmark
import dev.avatsav.linkding.data.model.SearchHistory
import dev.avatsav.linkding.navigation.LocalNavigator
import dev.avatsav.linkding.navigation.Route
import dev.avatsav.linkding.navigation.RouteNavigator
import dev.avatsav.linkding.navigation.rememberResultNavigator
import dev.avatsav.linkding.presenter.ObserveEffects
import dev.avatsav.linkding.ui.compose.ObserveSnackbar
import dev.avatsav.linkding.ui.compose.appearFromBottom
import dev.avatsav.linkding.ui.compose.disappearToBottom
import dev.avatsav.linkding.ui.compose.widgets.AnimatedVisibilityWithElevation
import dev.avatsav.linkding.ui.theme.BookmarkListTokens
import dev.avatsav.linkding.ui.theme.Material3ShapeDefaults
import kotlinx.coroutines.launch
import linkding_apps.features.bookmarks.ui.generated.resources.Res
import linkding_apps.features.bookmarks.ui.generated.resources.bookmarks_add
import linkding_apps.features.bookmarks.ui.generated.resources.bookmarks_all
import linkding_apps.features.bookmarks.ui.generated.resources.bookmarks_archive
import linkding_apps.features.bookmarks.ui.generated.resources.bookmarks_back
import linkding_apps.features.bookmarks.ui.generated.resources.bookmarks_clear
import linkding_apps.features.bookmarks.ui.generated.resources.bookmarks_delete
import linkding_apps.features.bookmarks.ui.generated.resources.bookmarks_edit
import linkding_apps.features.bookmarks.ui.generated.resources.bookmarks_search
import linkding_apps.features.bookmarks.ui.generated.resources.bookmarks_settings
import linkding_apps.features.bookmarks.ui.generated.resources.bookmarks_unarchive
import org.jetbrains.compose.resources.stringResource

@OptIn(
  ExperimentalMaterial3Api::class,
  ExperimentalMaterial3ExpressiveApi::class,
  ExperimentalComposeUiApi::class,
)
@Composable
fun BookmarksScreen(presenter: BookmarksPresenter, modifier: Modifier = Modifier) {
  val navigator = LocalNavigator.current
  val state by presenter.models.collectAsStateWithLifecycle()
  val eventSink = presenter::eventSink

  val tagsNavigator =
    rememberResultNavigator<Route.Tags, Route.Tags.Result> { result ->
      when (result) {
        is Confirmed -> {
          presenter.eventSink(BookmarksUiEvent.SetTags(result.selectedTags))
        }
        Dismissed -> {
          // No action needed on dismissal
        }
      }
    }

  ObserveEffects(presenter.effects) { effect ->
    when (effect) {
      AddBookmark -> navigator.goTo(Route.AddBookmark.New)
      is EditBookmark -> navigator.goTo(Route.AddBookmark.Edit(effect.bookmark.id))
      is EditPending -> navigator.goTo(Route.AddBookmark.Pending(effect.id))
      NavigateToSettings -> navigator.goTo(Route.Settings)
      is OpenBookmark -> navigator.goTo(Route.Url(effect.url))
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
  var selection by remember { mutableStateOf<BookmarkSelection?>(null) }
  val actionableBookmark = (selection as? BookmarkSelection.Saved)?.bookmark
  val actionablePending =
    state.pendingBookmarks
      .firstOrNull { it.id == (selection as? BookmarkSelection.Pending)?.id }
      ?.takeUnless { state.isSearchActive }
  val actionsVisible = actionableBookmark != null || actionablePending != null
  val snackbarHostState = remember { SnackbarHostState() }
  val searchBarState = rememberSearchBarState()
  val searchTextFieldState = rememberTextFieldState()
  val scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior()

  NavigationBackHandler(
    state = rememberNavigationEventState(NavigationEventInfo.None),
    isBackEnabled = actionsVisible,
    onBackCompleted = { selection = null },
  )

  ObserveSnackbar(
    snackbarMessage = state.snackbarMessage,
    snackbarHostState = snackbarHostState,
    onDismiss = { currentEventSink(BookmarksUiEvent.DismissSnackbar) },
  )

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
            Text(stringResource(Res.string.bookmarks_search))
          } else {
            Text("${searchTextFieldState.text}")
          }
        },
        leadingIcon = {
          if (searchBarState.isExpanded()) {
            IconButton(onClick = { scope.launch { searchBarState.animateToCollapsed() } }) {
              Icon(
                Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = stringResource(Res.string.bookmarks_back),
              )
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
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(Res.string.bookmarks_clear),
              )
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
            Icon(
              imageVector = Icons.Default.Settings,
              contentDescription = stringResource(Res.string.bookmarks_settings),
            )
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
        visible = !actionsVisible,
        enter = appearFromBottom(),
        exit = disappearToBottom(),
      ) { elevation ->
        FloatingActionButton(
          onClick = { currentEventSink(BookmarksUiEvent.AddBookmark) },
          elevation = FloatingActionButtonDefaults.elevation(defaultElevation = elevation),
        ) {
          Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = stringResource(Res.string.bookmarks_add),
          )
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
      BookmarkActionsToolbar(
        visible = actionsVisible,
        modifier =
          Modifier.align(Alignment.BottomCenter)
            .navigationBarsPadding()
            .offset(y = -ScreenOffset)
            .zIndex(1f),
      ) {
        BookmarkActions(
          bookmark = actionableBookmark,
          pending = actionablePending,
          onDismiss = { selection = null },
          eventSink = currentEventSink,
        )
      }

      LazyColumn(
        modifier =
          Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
            .floatingToolbarVerticalNestedScroll(
              expanded = actionsVisible,
              onExpand = { selection = null },
              onCollapse = { selection = null },
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

        if (!state.isSearchActive && state.pendingBookmarks.isNotEmpty()) {
          item(key = "pending_header") {
            PendingBookmarksHeader(
              count = state.pendingBookmarks.size,
              uploading =
                state.pendingBookmarks.any {
                  it.state == dev.avatsav.linkding.data.model.PendingBookmark.State.Uploading
                },
              onRetry = { currentEventSink(BookmarksUiEvent.RetryPending()) },
            )
          }
          items(
            count = state.pendingBookmarks.size,
            key = { "pending_${state.pendingBookmarks[it].id}" },
          ) { index ->
            val pending = state.pendingBookmarks[index]
            PendingBookmarkListItem(
              pending = pending,
              selected = actionablePending?.id == pending.id,
              openBookmark = { toOpen ->
                val wasSelected = actionablePending?.id == toOpen.id
                selection = null
                if (!wasSelected) currentEventSink(BookmarksUiEvent.Open(toOpen.bookmark.url))
              },
              toggleActions = { selection = BookmarkSelection.Pending(it.id) },
              modifier = Modifier.animateItem(),
            )
          }
        }

        if (
          !state.isSearchActive &&
            state.pendingBookmarks.isNotEmpty() &&
            state.bookmarks.itemCount > 0
        ) {
          item(key = "server_bookmarks_header") {
            Text(
              text = stringResource(Res.string.bookmarks_all),
              style = MaterialTheme.typography.titleSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(BookmarkListTokens.sectionHeaderPadding),
            )
          }
        }

        // Bookmark items
        items(count = state.bookmarks.itemCount, key = state.bookmarks.itemKey { it.id }) { index ->
          val bookmark = state.bookmarks[index]
          if (bookmark != null) {
            BookmarkListItem(
              modifier = Modifier.animateItem(),
              bookmark = bookmark,
              selected = actionableBookmark?.id == bookmark.id,
              openBookmark = { toOpen ->
                val wasSelected = actionableBookmark?.id == toOpen.id
                selection = null
                if (!wasSelected) currentEventSink(BookmarksUiEvent.Open(toOpen.url))
              },
              toggleActions = { selection = BookmarkSelection.Saved(it) },
            )
          }
        }
      }
    }
  }
}

@Composable
private fun RowScope.BookmarkActions(
  bookmark: Bookmark?,
  pending: PendingBookmark?,
  onDismiss: () -> Unit,
  eventSink: (BookmarksUiEvent) -> Unit,
) {
  if (pending != null) {
    PendingBookmarkActions(
      pending = pending,
      onDismiss = onDismiss,
      onEdit = { eventSink(BookmarksUiEvent.EditPending(it.id)) },
      onRetry = { eventSink(BookmarksUiEvent.RetryPending(it.id)) },
      onDiscard = { eventSink(BookmarksUiEvent.DiscardPending(it.id)) },
    )
  } else if (bookmark != null) {
    SavedBookmarkActions(
      bookmark = bookmark,
      onDismiss = onDismiss,
      editBookmark = { eventSink(BookmarksUiEvent.Edit(it)) },
      toggleArchive = { eventSink(BookmarksUiEvent.ToggleArchive(it)) },
      deleteBookmark = { eventSink(BookmarksUiEvent.Delete(it)) },
    )
  }
}

@Composable
private fun RowScope.SavedBookmarkActions(
  bookmark: Bookmark,
  onDismiss: () -> Unit,
  editBookmark: (Bookmark) -> Unit,
  toggleArchive: (Bookmark) -> Unit,
  deleteBookmark: (Bookmark) -> Unit,
) {
  IconButton(
    onClick = {
      onDismiss()
      toggleArchive(bookmark)
    }
  ) {
    Icon(
      if (bookmark.archived) Icons.Filled.Unarchive else Icons.Filled.Archive,
      contentDescription =
        stringResource(
          if (bookmark.archived) Res.string.bookmarks_unarchive else Res.string.bookmarks_archive
        ),
    )
  }
  IconButton(
    onClick = {
      onDismiss()
      deleteBookmark(bookmark)
    }
  ) {
    Icon(Icons.Filled.Delete, contentDescription = stringResource(Res.string.bookmarks_delete))
  }
  IconButton(
    onClick = {
      onDismiss()
      editBookmark(bookmark)
    }
  ) {
    Icon(Icons.Filled.Edit, contentDescription = stringResource(Res.string.bookmarks_edit))
  }
}

private sealed interface BookmarkSelection {
  data class Saved(val bookmark: Bookmark) : BookmarkSelection

  data class Pending(val id: Long) : BookmarkSelection
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
