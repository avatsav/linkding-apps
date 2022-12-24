package dev.avatsav.linkding.android.ui.bookmarks

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import dev.avatsav.linkding.android.R
import dev.avatsav.linkding.android.ui.theme.LinkdingTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreen(
    onAddBookmark: () -> Unit,
) {
    Scaffold(topBar = { TopAppBar(title = { Text(text = "Bookmarks") }) }, floatingActionButton = {
        FloatingActionButton(onClick = onAddBookmark) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_add),
                contentDescription = "Add Bookmark"
            )
        }
    }) {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            Text(text = "Hello world!")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BookmarkScreenPreview() {
    LinkdingTheme {
        BookmarksScreen(onAddBookmark = {})
    }
}