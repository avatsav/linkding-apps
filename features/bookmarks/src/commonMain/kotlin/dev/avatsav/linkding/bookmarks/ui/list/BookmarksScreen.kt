package dev.avatsav.linkding.bookmarks.ui.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
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
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.itemKey
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.overlay.ContentWithOverlays
import com.slack.circuit.overlay.LocalOverlayHost
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.AddBookmark
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.Delete
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.Open
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.RemoveTag
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.Search
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.SelectTag
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.SetBookmarkCategory
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.ShowSettings
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.ToggleArchive
import dev.avatsav.linkding.bookmarks.ui.list.widgets.BookmarkListItem
import dev.avatsav.linkding.bookmarks.ui.list.widgets.FiltersBar
import dev.avatsav.linkding.inject.UserScope
import dev.avatsav.linkding.ui.BookmarksScreen
import kotlinx.coroutines.launch

@CircuitInject(BookmarksScreen::class, UserScope::class)
@OptIn(ExperimentalMaterial3Api::class)
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

    val searchBarBgSurfaceElevation: Dp by animateDpAsState(
        if (scrolledToTop) 0.dp else 8.dp,
    )

    val searchBarContainerColor by animateColorAsState(
        if (scrolledToTop && !searchActive) {
            MaterialTheme.colorScheme.surfaceVariant
        } else {
            MaterialTheme.colorScheme.surface
        },
    )

    Scaffold(
        modifier = modifier,
        topBar = {
            Column(
                modifier = Modifier.background(
                    MaterialTheme.colorScheme.surfaceColorAtElevation(
                        searchBarBgSurfaceElevation,
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
                    inputField = {
                        SearchBarDefaults.InputField(
                            query = searchQuery,
                            onQueryChange = { searchQuery = it },
                            onSearch = {
                                eventSink(Search(searchQuery))
                                focusManager.clearFocus()
                            },
                            expanded = searchActive,
                            onExpandedChange = { searchActive = it },
                            enabled = true,
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
                            interactionSource = null,
                        )
                    },
                    expanded = searchActive,
                    onExpandedChange = { searchActive = it },
                    colors = SearchBarDefaults.colors(
                        containerColor = searchBarContainerColor,
                    ),
                    shape = SearchBarDefaults.inputFieldShape,
                    shadowElevation = SearchBarDefaults.ShadowElevation,
                    windowInsets = SearchBarDefaults.windowInsets,
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
        val refreshing by rememberUpdatedState(state.bookmarks.loadState.refresh == LoadState.Loading)

        PullToRefreshBox(
            modifier = Modifier.padding(
                top = paddingValues.calculateTopPadding(),
                start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
            ),
            isRefreshing = refreshing,
            onRefresh = { eventSink(BookmarksUiEvent.Refresh) },
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
                                            ActionResult.Confirmed -> eventSink(Delete(toDelete))
                                            ActionResult.Cancelled,
                                            ActionResult.Dismissed,
                                            -> dismissState.reset()
                                        }
                                    }
                                },
                                modifier = Modifier.animateItem(),
                            )
                        }
                    }
                }
            }
        }
    }
}
