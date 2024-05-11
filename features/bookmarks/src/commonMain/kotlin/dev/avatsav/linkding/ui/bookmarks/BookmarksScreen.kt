package dev.avatsav.linkding.ui.bookmarks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import app.cash.paging.compose.itemKey
import com.slack.circuit.overlay.LocalOverlayHost
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.ui.ui
import dev.avatsav.linkding.ui.BookmarksScreen
import dev.avatsav.linkding.ui.bookmarks.BookmarksUiEvent.AddBookmark
import dev.avatsav.linkding.ui.bookmarks.BookmarksUiEvent.Delete
import dev.avatsav.linkding.ui.bookmarks.BookmarksUiEvent.Open
import dev.avatsav.linkding.ui.bookmarks.BookmarksUiEvent.RemoveTag
import dev.avatsav.linkding.ui.bookmarks.BookmarksUiEvent.Search
import dev.avatsav.linkding.ui.bookmarks.BookmarksUiEvent.SelectTag
import dev.avatsav.linkding.ui.bookmarks.BookmarksUiEvent.SetBookmarkCategory
import dev.avatsav.linkding.ui.bookmarks.BookmarksUiEvent.ShowSettings
import dev.avatsav.linkding.ui.bookmarks.BookmarksUiEvent.ToggleArchive
import dev.avatsav.linkding.ui.bookmarks.widgets.BookmarkListItem
import dev.avatsav.linkding.ui.bookmarks.widgets.FiltersBar
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
    val eventSink = state.eventSink
    val overlayHost = LocalOverlayHost.current
    val focusManager = LocalFocusManager.current

    val scope = rememberCoroutineScope()

    var searchActive by rememberSaveable { mutableStateOf(false) }
    val searchBarHorizontalPadding: Dp by animateDpAsState(if (searchActive) 0.dp else 12.dp)
    val searchBarBottomPadding: Dp by animateDpAsState(if (searchActive) 0.dp else 12.dp)

    val listState = rememberLazyListState()
    val scrolledToTop by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
        }
    }

    val searchBarBackgroundElevation: Dp by animateDpAsState(
        targetValue = if (scrolledToTop) 0.dp else 8.dp,
        animationSpec = tween(),
    )
    val searchBarTonalElevation: Dp by animateDpAsState(
        targetValue = if (scrolledToTop && !searchActive) 8.dp else 0.dp,
        animationSpec = tween(),
    )

    Scaffold(
        modifier = modifier,
        topBar = {
            Column(
                modifier = Modifier.background(
                    MaterialTheme.colorScheme.surfaceColorAtElevation(
                        searchBarBackgroundElevation,
                    ),
                ),
            ) {
                var searchQuery by rememberSaveable { mutableStateOf("") }
                LaunchedEffect(searchActive) {
                    if (!searchActive) {
                        searchQuery = ""
                        eventSink(BookmarksUiEvent.ClearSearch)
                    }
                }

                SearchBar(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = searchBarHorizontalPadding)
                        .padding(bottom = searchBarBottomPadding),
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = {
                        eventSink(Search(searchQuery))
                        focusManager.clearFocus()
                    },
                    active = searchActive,
                    onActiveChange = { searchActive = it },
                    placeholder = { Text("Search for words or #tags") },
                    leadingIcon = {
                        Icon(
                            imageVector = if (state.isOnline) Icons.Default.Search else Icons.Default.CloudOff,
                            contentDescription = "Search Icon",
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                if (searchActive) {
                                    searchActive = false
                                } else {
                                    eventSink(ShowSettings)
                                }
                            },
                        ) {
                            Icon(
                                imageVector = if (searchActive) Icons.Default.Close else Icons.Outlined.Settings,
                                contentDescription = if (searchActive) "Close" else "Settings",
                            )
                        }
                    },
                    tonalElevation = searchBarTonalElevation,
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 56.dp),
                    ) {
                        items(
                            count = state.searchResults.itemCount,
                            key = state.searchResults.itemKey { it.id },
                        ) { index ->
                            val bookmark = state.searchResults[index]
                            if (bookmark != null) {
                                BookmarkListItem(
                                    bookmark = bookmark,
                                    openBookmark = { toOpen -> eventSink(Open(toOpen)) },
                                    toggleArchive = { toToggle, dismissState ->
                                        scope.launch {
                                            when (overlayHost.showArchiveBookmarkAction(toToggle)) {
                                                ActionResult.Confirmed -> eventSink(
                                                    ToggleArchive(
                                                        toToggle,
                                                    ),
                                                )

                                                ActionResult.Cancelled,
                                                ActionResult.Dismissed,
                                                -> dismissState.reset()
                                            }
                                        }
                                    },
                                    deleteBookmark = { toDelete, dismissState ->
                                        scope.launch {
                                            when (overlayHost.showDeleteBookmarkAction(toDelete)) {
                                                ActionResult.Confirmed -> eventSink(
                                                    Delete(
                                                        toDelete,
                                                    ),
                                                )

                                                ActionResult.Cancelled,
                                                ActionResult.Dismissed,
                                                -> dismissState.reset()
                                            }
                                        }
                                    },
                                )
                            }
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = !searchActive,
                enter = scaleIn(),
                exit = scaleOut(),
            ) {
                FloatingActionButton(onClick = { eventSink(AddBookmark) }) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Bookmark",
                    )
                }
            }
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(
                top = paddingValues.calculateTopPadding(),
                start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
            ).nestedScroll(state.pullToRefreshState.nestedScrollConnection).fillMaxSize(),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState,
                contentPadding = PaddingValues(bottom = 108.dp),
            ) {
                item(key = state.bookmarkCategory) {
                    FiltersBar(
                        selectedCategory = state.bookmarkCategory,
                        onCategorySelected = { eventSink(SetBookmarkCategory(it)) },
                        selectedTags = state.selectedTags.toList(),
                        onTagSelected = { eventSink(SelectTag(it)) },
                        onTagRemoved = { eventSink(RemoveTag(it)) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
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
                                        ActionResult.Confirmed -> eventSink(ToggleArchive(toToggle))
                                        ActionResult.Cancelled,
                                        ActionResult.Dismissed,
                                        -> dismissState.reset()
                                    }
                                }
                            },
                            deleteBookmark = { toDelete, dismissState ->
                                scope.launch {
                                    when (overlayHost.showDeleteBookmarkAction(toDelete)) {
                                        ActionResult.Confirmed -> eventSink(Delete(toDelete))
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
