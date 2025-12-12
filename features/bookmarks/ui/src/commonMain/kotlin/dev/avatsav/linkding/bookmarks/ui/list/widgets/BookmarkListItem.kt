package dev.avatsav.linkding.bookmarks.ui.list.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
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
    BookmarkItemContent(
      modifier =
        Modifier.background(
            if (selected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface
          )
          .combinedClickable(
            onClick = { openBookmark(bookmark) },
            onLongClick = { toggleActions(bookmark) },
          ),
      bookmark = bookmark,
    )
    HorizontalDivider(thickness = 0.5.dp)
  }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun BookmarkItemContent(bookmark: Bookmark, modifier: Modifier = Modifier) {
  Column(
    modifier = modifier.padding(16.dp).fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    Text(
      text = bookmark.title,
      color = MaterialTheme.colorScheme.primary,
      maxLines = 2,
      overflow = TextOverflow.Ellipsis,
      style = MaterialTheme.typography.titleMedium,
    )
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      Icon(
        imageVector = Icons.Default.Link,
        contentDescription = null,
        modifier = Modifier.size(12.dp),
      )
      Text(
        text = bookmark.urlHost,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
    }
    if (bookmark.description.isNotEmpty()) {
      Text(
        text = bookmark.description,
        style = MaterialTheme.typography.bodyMedium,
        maxLines = 3,
        overflow = TextOverflow.Ellipsis,
      )
    }
    LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
      items(items = bookmark.tags.toTypedArray()) { tagName ->
        Text(
          text = "#$tagName",
          color = MaterialTheme.colorScheme.tertiary,
          style = MaterialTheme.typography.labelSmallEmphasized,
        )
      }
    }
  }
}
