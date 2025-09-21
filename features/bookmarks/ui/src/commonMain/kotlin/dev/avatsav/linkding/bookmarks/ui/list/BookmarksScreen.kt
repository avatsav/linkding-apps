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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Unarchive
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarDefaults.ScreenOffset
import androidx.compose.material3.FloatingToolbarDefaults.floatingToolbarVerticalNestedScroll
import androidx.compose.material3.FloatingToolbarDefaults.vibrantFloatingToolbarColors
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumFloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
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
  var actionableBookmark by remember { mutableStateOf<Bookmark?>(null) }

  BackHandler(actionableBookmark != null) {
    actionableBookmark = null
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
      HorizontalFloatingToolbar(
        modifier = Modifier.align(Alignment.BottomEnd).navigationBarsPadding()
          .offset(x = -ScreenOffset, y = -ScreenOffset).zIndex(1f),
        expanded = actionableBookmark != null,
        floatingActionButton = {
          MediumFloatingActionButton(
            modifier = Modifier,
            containerColor = vibrantFloatingToolbarColors().fabContainerColor,
            contentColor = vibrantFloatingToolbarColors().fabContentColor,
            onClick = {
              if (actionableBookmark != null) {
                actionableBookmark = null
              } else {
                eventSink(AddBookmark)
              }
            },
            content = {
              if (actionableBookmark != null) {
                Icon(Icons.Filled.Close, "Close bookmark actions")
              } else Icon(Icons.Filled.Add, "Add bookmark")
            },
          )
        },
        colors = FloatingToolbarDefaults.vibrantFloatingToolbarColors(),
        content = {
          IconButton(
            onClick = {
              val bookmark = actionableBookmark ?: return@IconButton
              actionableBookmark = null
              eventSink(BookmarksUiEvent.ToggleArchive(bookmark = bookmark))
            },
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
              actionableBookmark = null
              eventSink(BookmarksUiEvent.Delete(bookmark = bookmark))
            },
          ) {
            Icon(Icons.Filled.Delete, contentDescription = "Delete bookmark")
          }
          IconButton(
            onClick = {
              val bookmark = actionableBookmark ?: return@IconButton
              actionableBookmark = null
              eventSink(BookmarksUiEvent.Edit(bookmark = bookmark))

            },
          ) {
            Icon(Icons.Filled.Edit, contentDescription = "Edit bookmark")
          }
        },
      )
      LazyColumn(
        modifier = Modifier.floatingToolbarVerticalNestedScroll(
          expanded = actionableBookmark != null,
          onExpand = { actionableBookmark = null },
          onCollapse = { actionableBookmark = null },
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
                  actionableBookmark = null
                } else {
                  actionableBookmark = null
                  eventSink(Open(toOpen))
                }
              },
              toggleActions = { bookmark -> actionableBookmark = bookmark },
            )
          }
        }
      }
    }
  }
}
