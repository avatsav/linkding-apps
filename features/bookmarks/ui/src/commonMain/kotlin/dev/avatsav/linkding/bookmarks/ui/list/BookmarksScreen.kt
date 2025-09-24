package dev.avatsav.linkding.bookmarks.ui.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarDefaults.ScreenOffset
import androidx.compose.material3.FloatingToolbarDefaults.floatingToolbarVerticalNestedScroll
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.paging.LoadState
import androidx.paging.compose.itemKey
import com.slack.circuit.codegen.annotations.CircuitInject
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.AddBookmark
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.Open
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.Refresh
import dev.avatsav.linkding.bookmarks.ui.list.widgets.BookmarkListItem
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.inject.UserScope
import dev.avatsav.linkding.ui.BookmarksScreen

@CircuitInject(BookmarksScreen::class, UserScope::class)
@OptIn(
  ExperimentalMaterial3Api::class,
  ExperimentalMaterial3ExpressiveApi::class,
  ExperimentalComposeUiApi::class,
)
@Composable
fun Bookmarks(state: BookmarksUiState, modifier: Modifier = Modifier) {
  val eventSink = state.eventSink
  val searchBarState = rememberSearchBarState()
  val actionableBookmark = remember { mutableStateOf<Bookmark?>(null) }

  BackHandler(actionableBookmark.value != null) {
    actionableBookmark.value = null
  }

  LaunchedEffect(searchBarState) {
    if (searchBarState.isExpanded()) actionableBookmark.value = null
  }

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
        visible = actionableBookmark.value == null && searchBarState.isCollapsed(),
        enter = appearFromBottom(),
        exit = disappearToBottom(),
      ) {
        FloatingActionButton(onClick = { eventSink(AddBookmark) }) {
          Icon(imageVector = Icons.Filled.Add, contentDescription = "Add bookmark")
        }
      }
    },
  ) { paddingValues ->
    val refreshing by rememberUpdatedState(state.bookmarkList.bookmarks.loadState.refresh == LoadState.Loading)
    val pullToRefreshState = rememberPullToRefreshState()
    PullToRefreshBox(
      modifier = Modifier.fillMaxSize().padding(
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

      ActionableBookmarkToolbar(
        actionableBookmark = actionableBookmark,
        editBookmark = { eventSink(BookmarksUiEvent.Edit(it)) },
        toggleArchive = { eventSink(BookmarksUiEvent.ToggleArchive(it)) },
        deleteBookmark = { eventSink(BookmarksUiEvent.Delete(it)) },
        modifier = Modifier.align(Alignment.BottomCenter).navigationBarsPadding()
          .offset(y = -ScreenOffset).zIndex(1f),
      )

      LazyColumn(
        modifier = Modifier.floatingToolbarVerticalNestedScroll(
          expanded = actionableBookmark.value != null,
          onExpand = { actionableBookmark.value = null },
          onCollapse = { actionableBookmark.value = null },
        ),
        state = rememberLazyListState(),
        contentPadding = PaddingValues(bottom = 108.dp),
      ) {
        items(
          count = state.bookmarkList.bookmarks.itemCount,
          key = state.bookmarkList.bookmarks.itemKey { it.id },
        ) { index ->
          val bookmark = state.bookmarkList.bookmarks[index]
          if (bookmark != null) {
            BookmarkListItem(
              modifier = Modifier.animateItem(),
              bookmark = bookmark,
              selected = actionableBookmark == bookmark,
              openBookmark = { toOpen ->
                if (actionableBookmark == toOpen) {
                  actionableBookmark.value = null
                } else {
                  actionableBookmark.value = null
                  eventSink(Open(toOpen))
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
  actionableBookmark: MutableState<Bookmark?>,
  editBookmark: (Bookmark) -> Unit,
  toggleArchive: (Bookmark) -> Unit,
  deleteBookmark: (Bookmark) -> Unit,
  modifier: Modifier,
) {
  AnimatedVisibility(
    modifier = modifier,
    visible = actionableBookmark.value != null,
    enter = appearFromBottom(),
    exit = disappearToBottom(),
  ) {
    HorizontalFloatingToolbar(
      expanded = true,
      colors = FloatingToolbarDefaults.vibrantFloatingToolbarColors(),
      content = {
        IconButton(
          onClick = {
            val bookmark = actionableBookmark.value ?: return@IconButton
            actionableBookmark.value = null
            toggleArchive(bookmark)
          },
        ) {
          val bookmark = actionableBookmark.value ?: return@IconButton
          if (bookmark.archived) {
            Icon(Icons.Filled.Unarchive, contentDescription = "Unarchive bookmark")
          } else {
            Icon(Icons.Filled.Archive, contentDescription = "Archive bookmark")
          }
        }
        IconButton(
          onClick = {
            val bookmark = actionableBookmark.value ?: return@IconButton
            actionableBookmark.value = null
            deleteBookmark(bookmark)
          },
        ) {
          Icon(Icons.Filled.Delete, contentDescription = "Delete bookmark")
        }
        IconButton(
          onClick = {
            val bookmark = actionableBookmark.value ?: return@IconButton
            actionableBookmark.value = null
            editBookmark(bookmark)
          },
        ) {
          Icon(Icons.Filled.Edit, contentDescription = "Edit bookmark")
        }
      },
    )
  }
}

fun appearFromBottom(): EnterTransition =
  slideInVertically(spring(Spring.DampingRatioMediumBouncy)) { it / 2 } + fadeIn()

fun disappearToBottom(): ExitTransition =
  slideOutVertically(spring(Spring.DampingRatioMediumBouncy)) { it / 2 } + fadeOut()
