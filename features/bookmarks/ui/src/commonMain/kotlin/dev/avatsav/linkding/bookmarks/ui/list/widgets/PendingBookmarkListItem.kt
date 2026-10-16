package dev.avatsav.linkding.bookmarks.ui.list.widgets

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.avatsav.linkding.data.model.PendingBookmark
import dev.avatsav.linkding.ui.compose.widgets.SmallCircularProgressIndicator
import linkding_apps.features.bookmarks.ui.generated.resources.Res
import linkding_apps.features.bookmarks.ui.generated.resources.pending_bookmark_attention
import linkding_apps.features.bookmarks.ui.generated.resources.pending_bookmark_retry
import linkding_apps.features.bookmarks.ui.generated.resources.pending_bookmark_upload
import linkding_apps.features.bookmarks.ui.generated.resources.pending_bookmarks_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun PendingBookmarksHeader(
  count: Int,
  uploading: Boolean,
  onRetry: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier.fillMaxWidth().padding(start = 16.dp, end = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
      stringResource(Res.string.pending_bookmarks_title, count),
      style = MaterialTheme.typography.titleSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.weight(1f),
    )
    TextButton(onClick = onRetry, enabled = !uploading) {
      Text(stringResource(Res.string.pending_bookmark_retry))
    }
  }
}

@Composable
fun PendingBookmarkListItem(
  pending: PendingBookmark,
  selected: Boolean,
  openBookmark: (PendingBookmark) -> Unit,
  toggleActions: (PendingBookmark) -> Unit,
  modifier: Modifier = Modifier,
) {
  val uploading = pending.state == PendingBookmark.State.Uploading
  val needsAttention =
    pending.state == PendingBookmark.State.Failed ||
      pending.state == PendingBookmark.State.Uncertain
  BookmarkRow(
    title = pending.bookmark.title?.takeIf { it.isNotBlank() } ?: pending.bookmark.url,
    urlHost = pending.bookmark.url.substringAfter("://").substringBefore('/'),
    description = pending.bookmark.description.orEmpty(),
    tags = pending.bookmark.tags,
    selected = selected,
    onClick = { openBookmark(pending) },
    onLongClick = { toggleActions(pending) },
    modifier = modifier,
    leadingIcon = {
      if (uploading) SmallCircularProgressIndicator()
      else
        Icon(
          if (needsAttention) Icons.Outlined.ErrorOutline else Icons.Outlined.CloudUpload,
          contentDescription =
            stringResource(
              if (needsAttention) Res.string.pending_bookmark_attention
              else Res.string.pending_bookmark_upload
            ),
          tint =
            if (needsAttention) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(16.dp),
        )
    },
  )
}
