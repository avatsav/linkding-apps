package dev.avatsav.linkding.ui.bookmarks

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import app.cash.paging.compose.itemKey
import com.slack.circuit.overlay.LocalOverlayHost
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.ui.ui
import dev.avatsav.linkding.data.model.BookmarkCategory
import dev.avatsav.linkding.ui.BookmarksScreen
import dev.avatsav.linkding.ui.bookmarks.BookmarksUiEvent.AddBookmark
import dev.avatsav.linkding.ui.bookmarks.BookmarksUiEvent.Delete
import dev.avatsav.linkding.ui.bookmarks.BookmarksUiEvent.Open
import dev.avatsav.linkding.ui.bookmarks.BookmarksUiEvent.ToggleArchive
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
    val eventSink = state.eventSink
    val bookmarkCategories = BookmarkCategory.entries
    val overlayHost = LocalOverlayHost.current

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
                    color = MaterialTheme.colorScheme.surfaceColorAtElevation(
                        searchBarBackgroundElevation,
                    ),
                ),
            ) {
                SearchBar(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = searchBarHorizontalPadding)
                        .padding(bottom = searchBarBottomPadding),
                    query = "",
                    onQueryChange = { },
                    onSearch = { searchActive = false },
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
                                    eventSink(BookmarksUiEvent.ShowSettings)
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
                ) {}
            }
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
        Box(
            modifier = Modifier.padding(
                top = paddingValues.calculateTopPadding(),
                start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                bottom = 0.dp,
            )
                .nestedScroll(state.pullToRefreshState.nestedScrollConnection).fillMaxSize(),
        ) {
            // Content padding: 56dp(FAB) + 32(Top+Bottom Padding)
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState,
                contentPadding = PaddingValues(bottom = 88.dp),
            ) {
                item {
                    BookmarkCategoryFilter(
                        categories = bookmarkCategories,
                        selected = state.bookmarkCategory,
                        onSelected = { eventSink(BookmarksUiEvent.SetBookmarkCategory(it)) },
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
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

@Composable
fun BookmarkCategoryFilter(
    categories: List<BookmarkCategory>,
    selected: BookmarkCategory,
    onSelected: (BookmarkCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(categories) {
            FilterChip(
                selected = selected == it,
                onClick = { onSelected(it) },
                label = { Text(it.name) },
            )
        }
    }
}
