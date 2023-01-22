package dev.avatsav.linkding.android.ui.bookmarks

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.avatsav.linkding.android.extensions.OnEndReached
import dev.avatsav.linkding.android.theme.LinkdingTheme
import dev.avatsav.linkding.android.ui.destinations.AddBookmarkScreenDestination
import dev.avatsav.linkding.ui.Loading
import dev.avatsav.linkding.ui.PageStatus
import dev.avatsav.linkding.ui.bookmarks.BookmarkViewItem
import dev.avatsav.linkding.ui.bookmarks.BookmarksViewModel
import dev.avatsav.linkding.ui.bookmarks.BookmarksViewState
import dev.avatsav.linkding.ui.onPagedContent
import org.koin.androidx.compose.koinViewModel

@Composable
@Destination
fun BookmarksScreen(navigator: DestinationsNavigator) {
    val viewModel: BookmarksViewModel = koinViewModel()
    BookmarksScreen(viewModel = viewModel, onAddBookmark = {
        navigator.navigate(AddBookmarkScreenDestination(sharedUrl = null))
    })
}

@Composable
fun BookmarksScreen(
    viewModel: BookmarksViewModel,
    onAddBookmark: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    BookmarksScreen(
        viewState = state,
        onRefresh = { viewModel.load() },
        onEndReached = { viewModel.loadMore() },
        onAddBookmark = onAddBookmark,
        onQueryChanged = { },
    )
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class,
)
@Composable
fun BookmarksScreen(
    modifier: Modifier = Modifier,
    viewState: BookmarksViewState,
    onRefresh: () -> Unit,
    onEndReached: () -> Unit,
    onAddBookmark: () -> Unit,
    onQueryChanged: (String) -> Unit,
) {
    val bookmarksState = viewState.bookmarksState
    var contentStatus = PageStatus.HasMore
    var bookmarkItems = emptyList<BookmarkViewItem>()
    bookmarksState onPagedContent { pagedContent ->
        contentStatus = pagedContent.status
        bookmarkItems = pagedContent.value
    }

    val listState = rememberLazyListState()
    val pullRefreshState = rememberPullRefreshState(bookmarksState is Loading, onRefresh)

    Scaffold(
        modifier = modifier,
        topBar = {
            SearchBar(
                searchClicked = { /*TODO*/ },
                menuClicked = { /*TODO*/ },
                tagsClicked = { /*TODO*/ },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddBookmark) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Bookmark",
                )
            }
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .pullRefresh(pullRefreshState)
                .fillMaxSize(),
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                state = listState,
                contentPadding = PaddingValues(bottom = 88.dp), // 56dp(FAB) + 32(Top+Bottom Padding)
            ) {
                items(items = bookmarkItems, key = { it.id }) { bookmark ->
                    BookmarkItem(
                        bookmark = bookmark,
                        onClicked = {},
                        onTagClicked = {},
                    )
                }
                if (contentStatus == PageStatus.HasMore || contentStatus == PageStatus.LoadingMore) {
                    item {
                        LoadingMoreItem()
                    }
                }
            }
            PullRefreshIndicator(
                refreshing = bookmarksState is Loading,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                backgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
                contentColor = MaterialTheme.colorScheme.contentColorFor(MaterialTheme.colorScheme.surface),
            )
            listState.OnEndReached {
                onEndReached()
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun BookmarkScreenPreview() {
    val sampleBookmarkList = listOf(
        BookmarkViewItem(
            id = 1,
            title = "The Road to Success",
            description = "A step by step tutorial in how to navigate your way to the road to success!",
            url = "www.roadtosuccess.com",
            urlHostName = "www.roadtosuccess.com",
        ),
        BookmarkViewItem(
            id = 1,
            title = "From Darkness to Light",
            description = "Darkness is just the beginning.",
            url = "www.darkness2light.com",
            urlHostName = "www.roadtosuccess.com",
        ),
        BookmarkViewItem(
            id = 1,
            title = "The Power of Perseverance",
            url = "www.perseverancepower.com",
            description = "",
            urlHostName = "www.roadtosuccess.com",
        ),
        BookmarkViewItem(
            id = 1,
            title = "The Art of Courage",
            url = "www.courageart.com",
            description = "",
            urlHostName = "www.roadtosuccess.com",
        ),
        BookmarkViewItem(
            id = 1,
            title = "The Journey Within",
            url = "www.journeywithin.com",
            description = "",
            urlHostName = "www.roadtosuccess.com",
        ),
        BookmarkViewItem(
            id = 1,
            title = "Finding Your Way",
            url = "www.findingyourway.com",
            description = "",
            urlHostName = "www.roadtosuccess.com",
        ),
    )
    LinkdingTheme {
        Surface {
            BookmarksScreen(
                viewState = BookmarksViewState.Initial,
                onRefresh = {},
                onEndReached = {},
                onAddBookmark = {},
                onQueryChanged = {},
            )
        }
    }
}
