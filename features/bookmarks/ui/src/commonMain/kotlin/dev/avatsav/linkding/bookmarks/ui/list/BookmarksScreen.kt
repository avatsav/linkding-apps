package dev.avatsav.linkding.bookmarks.ui.list

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Unarchive
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
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
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.ui.compose.appearFromBottom
import dev.avatsav.linkding.ui.compose.disappearToBottom
import dev.avatsav.linkding.ui.compose.widgets.AnimatedVisibilityWithElevation

@OptIn(
  ExperimentalMaterial3Api::class,
  ExperimentalMaterial3ExpressiveApi::class,
  ExperimentalComposeUiApi::class,
)
@Composable
fun Bookmarks(viewModel: BookmarksViewModel, modifier: Modifier = Modifier) {
  val state by viewModel.models.collectAsStateWithLifecycle()
  val eventSink = viewModel::eventSink
  val feedEventSink = viewModel.feedEventSink
  val searchEventSink = viewModel.searchEventSink
  Bookmarks(state, modifier, eventSink, feedEventSink, searchEventSink)
}

@OptIn(
  ExperimentalMaterial3Api::class,
  ExperimentalMaterial3ExpressiveApi::class,
  ExperimentalComposeUiApi::class,
)
@Composable
fun Bookmarks(
  state: BookmarksUiState,
  modifier: Modifier = Modifier,
  eventSink: (BookmarksUiEvent) -> Unit,
  feedEventSink: (BookmarkFeedUiEvent) -> Unit,
  searchEventSink: (BookmarkSearchUiEvent) -> Unit,
) {
  val searchBarState = rememberSearchBarState()
  val actionableBookmark = remember { mutableStateOf<Bookmark?>(null) }
  val snackbarHostState = remember { SnackbarHostState() }

  NavigationBackHandler(
    state = rememberNavigationEventState(NavigationEventInfo.None),
    isBackEnabled = actionableBookmark.value != null,
    onBackCompleted = { actionableBookmark.value = null },
  )

  LaunchedEffect(searchBarState) {
    if (searchBarState.isExpanded()) actionableBookmark.value = null
  }

  // Handle snackbar messages
  LaunchedEffect(state.feedState.snackbarMessage) {
    state.feedState.snackbarMessage?.let { message ->
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
          feedEventSink(BookmarkFeedUiEvent.DismissSnackbar)
        }
      }
    }
  }

  Scaffold(
    modifier = modifier,
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
      SearchTopBar(
        searchBarState = searchBarState,
        searchState = state.searchState,
        onShowSettings = { eventSink(BookmarksUiEvent.ShowSettings) },
        eventSink = searchEventSink,
      )
    },
    floatingActionButton = {
      AnimatedVisibilityWithElevation(
        visible = actionableBookmark.value == null && searchBarState.isCollapsed(),
        enter = appearFromBottom(),
        exit = disappearToBottom(),
      ) { elevation ->
        FloatingActionButton(
          onClick = { eventSink(BookmarksUiEvent.AddBookmark) },
          elevation = FloatingActionButtonDefaults.elevation(defaultElevation = elevation),
        ) {
          Icon(imageVector = Icons.Filled.Add, contentDescription = "Add bookmark")
        }
      }
    },
  ) { paddingValues ->
    val refreshing by
      rememberUpdatedState(state.feedState.bookmarks.loadState.refresh == LoadState.Loading)
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
      onRefresh = { feedEventSink(BookmarkFeedUiEvent.Refresh) },
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
        editBookmark = { feedEventSink(BookmarkFeedUiEvent.Edit(it)) },
        toggleArchive = { feedEventSink(BookmarkFeedUiEvent.ToggleArchive(it)) },
        deleteBookmark = { feedEventSink(BookmarkFeedUiEvent.Delete(it)) },
        modifier =
          Modifier.align(Alignment.BottomCenter)
            .navigationBarsPadding()
            .offset(y = -ScreenOffset)
            .zIndex(1f),
      )

      LazyColumn(
        modifier =
          Modifier.floatingToolbarVerticalNestedScroll(
            expanded = actionableBookmark.value != null,
            onExpand = { actionableBookmark.value = null },
            onCollapse = { actionableBookmark.value = null },
          ),
        state = rememberLazyListState(),
        contentPadding = PaddingValues(bottom = 108.dp),
      ) {
        items(
          count = state.feedState.bookmarks.itemCount,
          key = state.feedState.bookmarks.itemKey { it.id },
        ) { index ->
          val bookmark = state.feedState.bookmarks[index]
          if (bookmark != null) {
            BookmarkListItem(
              modifier = Modifier.animateItem(),
              bookmark = bookmark,
              selected = actionableBookmark.value == bookmark,
              openBookmark = { toOpen ->
                if (actionableBookmark == toOpen) {
                  actionableBookmark.value = null
                } else {
                  actionableBookmark.value = null
                  feedEventSink(BookmarkFeedUiEvent.Open(toOpen))
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
fun ActionableBookmarkToolbar(
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
