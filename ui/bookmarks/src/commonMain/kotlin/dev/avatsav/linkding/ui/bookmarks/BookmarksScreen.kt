package dev.avatsav.linkding.ui.bookmarks

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.cash.paging.compose.itemKey
import com.slack.circuit.overlay.LocalOverlayHost
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.ui.ui
import dev.avatsav.linkding.ui.BookmarksScreen
import dev.avatsav.linkding.ui.bookmarks.BookmarksUiEvent.AddBookmark
import dev.avatsav.linkding.ui.bookmarks.BookmarksUiEvent.Archive
import dev.avatsav.linkding.ui.bookmarks.BookmarksUiEvent.Delete
import dev.avatsav.linkding.ui.bookmarks.BookmarksUiEvent.Open
import dev.avatsav.linkding.ui.bookmarks.widgets.BookmarkListItem
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class BookmarksUiFactory : Ui.Factory {
    override fun create(screen: Screen, context: CircuitContext): Ui<*>? {
        return when (screen) {
            is BookmarksScreen -> {
                ui<BookmarksUiState> { state, modifier ->
                    Bookmarks(state, modifier)
                }
            }

            else -> null
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Bookmarks(
    state: BookmarksUiState,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val eventSink = state.eventSink
    val overlayHost = LocalOverlayHost.current

    var searchActive by rememberSaveable { mutableStateOf(false) }
    val searchBarHorizontalPadding: Dp by animateDpAsState(if (searchActive) 0.dp else 12.dp)

    Scaffold(
        modifier = modifier,
        topBar = {
            SearchBar(
                modifier = Modifier.fillMaxWidth().padding(horizontal = searchBarHorizontalPadding),
                query = "",
                onQueryChange = { },
                onSearch = { searchActive = false },
                active = searchActive,
                onActiveChange = { searchActive = it },
                placeholder = { Text("Search for words or #tags") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
                trailingIcon = {
                    IconButton(onClick = { if (searchActive) searchActive = false }) {
                        Icon(
                            imageVector = if (searchActive) Icons.Default.Close else Icons.Default.MoreVert,
                            contentDescription = null,
                        )
                    }
                },
            ) {}
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { eventSink(AddBookmark) }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Bookmark",
                )
            }
        },
    ) { paddingValues ->

        val listState = rememberLazyListState()
        Box(
            modifier = Modifier.padding(paddingValues)
                .nestedScroll(state.pullToRefreshState.nestedScrollConnection).fillMaxSize(),
        ) {
            // Content padding: 56dp(FAB) + 32(Top+Bottom Padding)
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState,
                contentPadding = PaddingValues(bottom = 88.dp),
            ) {
                items(
                    count = state.bookmarks.itemCount,
                    key = state.bookmarks.itemKey { it.localId },
                ) { index ->
                    val bookmark = state.bookmarks[index]
                    if (bookmark != null) {
                        BookmarkListItem(
                            bookmark = bookmark,
                            openBookmark = { toOpen -> eventSink(Open(toOpen)) },
                            toggleArchive = { toToggle, dismissState ->
                                scope.launch {
                                    when (overlayHost.showArchiveBookmarkAction(toToggle)) {
                                        ActionResult.Confirmed -> {
                                            eventSink(Archive(toToggle))
                                        }

                                        ActionResult.Cancelled,
                                        ActionResult.Dismissed,
                                        -> dismissState.reset()
                                    }
                                }
                            },
                            deleteBookmark = { toDelete, dismissState ->
                                scope.launch {
                                    when (overlayHost.showDeleteBookmarkAction(toDelete)) {
                                        ActionResult.Confirmed -> {
                                            eventSink(Delete(toDelete))
                                        }

                                        ActionResult.Cancelled,
                                        ActionResult.Dismissed,
                                        -> dismissState.reset()
                                    }
                                }
                            },
                            modifier = Modifier.animateItemPlacement(),
                        )
                    }
                }
            }
            PullToRefreshContainer(
                modifier = Modifier.align(Alignment.TopCenter),
                state = state.pullToRefreshState,
            )
        }
    }
}
