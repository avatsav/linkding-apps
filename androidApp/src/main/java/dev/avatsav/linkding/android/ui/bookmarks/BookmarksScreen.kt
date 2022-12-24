package dev.avatsav.linkding.android.ui.bookmarks

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.avatsav.linkding.android.R
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
    BookmarksScreen(state, onAddBookmark)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreen(
    state: BookmarksPresenter.ViewState,
    onAddBookmark: () -> Unit,
) {

    Scaffold(topBar = { TopAppBar(title = { Text(text = "Bookmarks") }) }, floatingActionButton = {
        FloatingActionButton(onClick = onAddBookmark) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_add),
                contentDescription = "Add Bookmark"
            )
        }
    }) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (state.loading) {
                CircularProgressIndicator()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.bookmarks) { bookmark ->
                        Text(
                            text = bookmark.title, modifier = Modifier.padding(16.dp)
                        )
                        Divider()
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BookmarkScreenPreview() {
    LinkdingTheme {
        BookmarksScreen(
            state = BookmarksPresenter.ViewState(false, sampleBookmarkList),
            onAddBookmark = {})
    }
}


private val sampleBookmarkList = listOf<Bookmark>(
    Bookmark(title = "The Road to Success", url = "www.roadtosuccess.com"),
    Bookmark(title = "From Darkness to Light", url = "www.darkness2light.com"),
    Bookmark(title = "The Power of Perseverance", url = "www.perseverancepower.com"),
    Bookmark(title = "The Art of Courage", url = "www.courageart.com"),
    Bookmark(title = "The Journey Within", url = "www.journeywithin.com"),
    Bookmark(title = "Finding Your Way", url = "www.findingyourway.com")
)