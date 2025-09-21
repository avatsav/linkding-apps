package dev.avatsav.linkding.bookmarks.ui.list.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.avatsav.linkding.data.model.Bookmark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarkListItem(
  bookmark: Bookmark,
  selected: Boolean,
  openBookmark: (Bookmark) -> Unit,
  toggleActions: (Bookmark) -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(modifier) {
    BookmarkContent(
      modifier = Modifier.background(
        if (selected) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surface,
      ).combinedClickable(
        onClick = { openBookmark(bookmark) },
        onLongClick = { toggleActions(bookmark) },
      ),
      bookmark = bookmark,
    )
    HorizontalDivider(thickness = 0.5.dp)
  }
}
