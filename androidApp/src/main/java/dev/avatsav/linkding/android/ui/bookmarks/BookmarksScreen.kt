package dev.avatsav.linkding.android.ui.bookmarks

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.avatsav.linkding.android.ui.theme.LinkdingTheme
import dev.avatsav.linkding.bookmark.domain.Bookmark
import dev.avatsav.linkding.ui.BookmarksPresenter

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun BookmarksScreen(
    presenter: BookmarksPresenter,
    onAddBookmark: () -> Unit,
) {
    val state: BookmarksPresenter.ViewState by presenter.state.collectAsStateWithLifecycle()
    BookmarksScreen(state = state, onAddBookmark = onAddBookmark)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreen(
    modifier: Modifier = Modifier,
    state: BookmarksPresenter.ViewState,
    onAddBookmark: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            SearchBar(
                searchClicked = { /*TODO*/ },
                menuClicked = { /*TODO*/ },
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
                        Text(text = bookmark.getTitleForUi(), modifier = Modifier.padding(16.dp))
                        Divider()
                    }
                }
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun BookmarkScreenPreview() {
    val sampleBookmarkList = listOf(
        Bookmark(id = 1, title = "The Road to Success", url = "www.roadtosuccess.com"),
        Bookmark(id = 1, title = "From Darkness to Light", url = "www.darkness2light.com"),
        Bookmark(id = 1, title = "The Power of Perseverance", url = "www.perseverancepower.com"),
        Bookmark(id = 1, title = "The Art of Courage", url = "www.courageart.com"),
        Bookmark(id = 1, title = "The Journey Within", url = "www.journeywithin.com"),
        Bookmark(id = 1, title = "Finding Your Way", url = "www.findingyourway.com")
    )
    LinkdingTheme {
        BookmarksScreen(state = BookmarksPresenter.ViewState(false, sampleBookmarkList),
            onAddBookmark = {})
    }
}