package dev.avatsav.linkding.android.ui.screens.bookmarks

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.avatsav.linkding.android.ui.destinations.AddBookmarkScreenDestination
import dev.avatsav.linkding.android.ui.theme.LinkdingTheme
import dev.avatsav.linkding.ui.viewmodel.BookmarkViewItem
import dev.avatsav.linkding.ui.viewmodel.BookmarksViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
@Destination
fun BookmarksScreen(navigator: DestinationsNavigator) {
    val viewModel: BookmarksViewModel = koinViewModel()
    
    BookmarksScreen(viewModel = viewModel, onAddBookmark = {
        navigator.navigate(AddBookmarkScreenDestination(sharedUrl = null))
    })
}

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun BookmarksScreen(
    viewModel: BookmarksViewModel,
    onAddBookmark: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    BookmarksScreen(
        state = state, onAddBookmark = onAddBookmark
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreen(
    modifier: Modifier = Modifier,
    state: BookmarksViewModel.ViewState,
    onAddBookmark: () -> Unit,
) {
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
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Bookmark")
            }
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (state.loading) {
                CircularProgressIndicator()
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(items = state.bookmarks, key = { it.id }) { bookmark ->
                        BookmarkItem(bookmark = bookmark, onClicked = {}, onTagClicked = {})
                        Divider(thickness = 0.5.dp)
                    }
                }
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
            urlHostName = "www.roadtosuccess.com"
        ), BookmarkViewItem(
            id = 1,
            title = "From Darkness to Light",
            description = "Darkness is just the beginning.",
            url = "www.darkness2light.com",
            urlHostName = "www.roadtosuccess.com"
        ), BookmarkViewItem(
            id = 1,
            title = "The Power of Perseverance",
            url = "www.perseverancepower.com",
            description = "",
            urlHostName = "www.roadtosuccess.com"
        ), BookmarkViewItem(
            id = 1,
            title = "The Art of Courage",
            url = "www.courageart.com",
            description = "",
            urlHostName = "www.roadtosuccess.com"
        ), BookmarkViewItem(
            id = 1,
            title = "The Journey Within",
            url = "www.journeywithin.com",
            description = "",
            urlHostName = "www.roadtosuccess.com"
        ), BookmarkViewItem(
            id = 1,
            title = "Finding Your Way",
            url = "www.findingyourway.com",
            description = "",
            urlHostName = "www.roadtosuccess.com"
        )
    )
    LinkdingTheme {
        Surface {
            BookmarksScreen(
                state = BookmarksViewModel.ViewState(false, sampleBookmarkList),
                onAddBookmark = {})
        }
    }
}