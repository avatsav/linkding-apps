package dev.avatsav.linkding.bookmarks.ui.list.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.avatsav.linkding.data.model.PendingBookmark
import dev.avatsav.linkding.ui.compose.widgets.SmallCircularProgressIndicator
import linkding_apps.features.bookmarks.ui.generated.resources.Res
import linkding_apps.features.bookmarks.ui.generated.resources.bookmarks_edit
import linkding_apps.features.bookmarks.ui.generated.resources.pending_bookmark_actions
import linkding_apps.features.bookmarks.ui.generated.resources.pending_bookmark_attention
import linkding_apps.features.bookmarks.ui.generated.resources.pending_bookmark_cancel
import linkding_apps.features.bookmarks.ui.generated.resources.pending_bookmark_discard
import linkding_apps.features.bookmarks.ui.generated.resources.pending_bookmark_discard_message
import linkding_apps.features.bookmarks.ui.generated.resources.pending_bookmark_discard_title
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
  onEdit: () -> Unit,
  onRetry: () -> Unit,
  onDiscard: () -> Unit,
  modifier: Modifier = Modifier,
) {
  var menuExpanded by rememberSaveable { mutableStateOf(false) }
  var confirmDiscard by rememberSaveable { mutableStateOf(false) }
  val uploading = pending.state == PendingBookmark.State.Uploading
  val needsAttention =
    pending.state == PendingBookmark.State.Failed ||
      pending.state == PendingBookmark.State.Uncertain
  Column(modifier) {
    Row(
      Modifier.fillMaxWidth()
        .clickable(enabled = !uploading, onClick = onEdit)
        .padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
          pending.bookmark.title?.takeIf { it.isNotBlank() } ?: pending.bookmark.url,
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.primary,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
        )
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
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
          Text(
            pending.bookmark.url.substringAfter("://").substringBefore('/'),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
        }
      }
      Column {
        IconButton(onClick = { menuExpanded = true }, enabled = !uploading) {
          Icon(
            Icons.Default.MoreVert,
            contentDescription = stringResource(Res.string.pending_bookmark_actions),
          )
        }
        DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
          DropdownMenuItem(
            text = { Text(stringResource(Res.string.bookmarks_edit)) },
            onClick = {
              menuExpanded = false
              onEdit()
            },
          )
          DropdownMenuItem(
            text = { Text(stringResource(Res.string.pending_bookmark_retry)) },
            onClick = {
              menuExpanded = false
              onRetry()
            },
          )
          DropdownMenuItem(
            text = { Text(stringResource(Res.string.pending_bookmark_discard)) },
            onClick = {
              menuExpanded = false
              confirmDiscard = true
            },
          )
        }
      }
    }
    HorizontalDivider()
  }
  if (confirmDiscard)
    AlertDialog(
      onDismissRequest = { confirmDiscard = false },
      title = { Text(stringResource(Res.string.pending_bookmark_discard_title)) },
      text = { Text(stringResource(Res.string.pending_bookmark_discard_message)) },
      confirmButton = {
        TextButton(
          onClick = {
            confirmDiscard = false
            onDiscard()
          }
        ) {
          Text(stringResource(Res.string.pending_bookmark_discard))
        }
      },
      dismissButton = {
        TextButton(onClick = { confirmDiscard = false }) {
          Text(stringResource(Res.string.pending_bookmark_cancel))
        }
      },
    )
}
