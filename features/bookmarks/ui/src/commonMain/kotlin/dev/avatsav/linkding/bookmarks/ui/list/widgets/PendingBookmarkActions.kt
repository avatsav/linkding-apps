package dev.avatsav.linkding.bookmarks.ui.list.widgets

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import dev.avatsav.linkding.data.model.PendingBookmark
import linkding_apps.features.bookmarks.ui.generated.resources.Res
import linkding_apps.features.bookmarks.ui.generated.resources.bookmarks_edit
import linkding_apps.features.bookmarks.ui.generated.resources.pending_bookmark_cancel
import linkding_apps.features.bookmarks.ui.generated.resources.pending_bookmark_discard
import linkding_apps.features.bookmarks.ui.generated.resources.pending_bookmark_discard_message
import linkding_apps.features.bookmarks.ui.generated.resources.pending_bookmark_discard_title
import linkding_apps.features.bookmarks.ui.generated.resources.pending_bookmark_retry
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun RowScope.PendingBookmarkActions(
  pending: PendingBookmark,
  onDismiss: () -> Unit,
  onEdit: (PendingBookmark) -> Unit,
  onRetry: (PendingBookmark) -> Unit,
  onDiscard: (PendingBookmark) -> Unit,
) {
  var confirmDiscard by rememberSaveable(pending.id) { mutableStateOf(false) }
  val enabled = pending.state != PendingBookmark.State.Uploading
  IconButton(
    enabled = enabled,
    onClick = {
      onDismiss()
      onRetry(pending)
    },
  ) {
    Icon(
      Icons.Default.Refresh,
      contentDescription = stringResource(Res.string.pending_bookmark_retry),
    )
  }
  IconButton(enabled = enabled, onClick = { confirmDiscard = true }) {
    Icon(
      Icons.Default.Delete,
      contentDescription = stringResource(Res.string.pending_bookmark_discard),
    )
  }
  IconButton(
    enabled = enabled,
    onClick = {
      onDismiss()
      onEdit(pending)
    },
  ) {
    Icon(Icons.Default.Edit, contentDescription = stringResource(Res.string.bookmarks_edit))
  }
  if (confirmDiscard) {
    AlertDialog(
      onDismissRequest = { confirmDiscard = false },
      title = { Text(stringResource(Res.string.pending_bookmark_discard_title)) },
      text = { Text(stringResource(Res.string.pending_bookmark_discard_message)) },
      confirmButton = {
        TextButton(
          enabled = enabled,
          onClick = {
            confirmDiscard = false
            onDismiss()
            onDiscard(pending)
          },
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
}
