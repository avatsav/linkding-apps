package dev.avatsav.linkding.bookmarks.ui.list.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import dev.avatsav.linkding.data.model.PendingBookmark
import linkding_apps.features.bookmarks.ui.generated.resources.Res
import linkding_apps.features.bookmarks.ui.generated.resources.bookmarks_edit
import linkding_apps.features.bookmarks.ui.generated.resources.pending_bookmark_cancel
import linkding_apps.features.bookmarks.ui.generated.resources.pending_bookmark_discard
import linkding_apps.features.bookmarks.ui.generated.resources.pending_bookmark_discard_message
import linkding_apps.features.bookmarks.ui.generated.resources.pending_bookmark_discard_title
import linkding_apps.features.bookmarks.ui.generated.resources.pending_bookmark_overwrite
import linkding_apps.features.bookmarks.ui.generated.resources.pending_bookmark_overwrite_message
import linkding_apps.features.bookmarks.ui.generated.resources.pending_bookmark_overwrite_title
import linkding_apps.features.bookmarks.ui.generated.resources.pending_bookmark_retry
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun RowScope.PendingBookmarkActions(
  pending: PendingBookmark,
  onDismiss: () -> Unit,
  active: Boolean,
  onEdit: (PendingBookmark) -> Unit,
  onRetry: (PendingBookmark) -> Unit,
  onOverwrite: (PendingBookmark) -> Unit,
  onDiscard: (PendingBookmark) -> Unit,
) {
  var confirmation by rememberSaveable(pending.id, active) { mutableStateOf<Confirmation?>(null) }
  val enabled = active && pending.state != PendingBookmark.State.Uploading
  val alreadyExists = pending.state == PendingBookmark.State.AlreadyExists
  if (alreadyExists) {
    TextButton(enabled = enabled, onClick = { confirmation = Confirmation.Overwrite }) {
      ConflictActionLabel(Icons.Default.SwapVert, Res.string.pending_bookmark_overwrite)
    }
    FilledTonalButton(
      enabled = enabled,
      onClick = { confirmation = Confirmation.Discard },
      colors =
        ButtonDefaults.filledTonalButtonColors(
          containerColor = MaterialTheme.colorScheme.surfaceContainer,
          contentColor = MaterialTheme.colorScheme.onSurface,
        ),
    ) {
      ConflictActionLabel(Icons.Default.Delete, Res.string.pending_bookmark_discard)
    }
  } else {
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
    IconButton(enabled = enabled, onClick = { confirmation = Confirmation.Discard }) {
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
  }
  confirmation?.let { action ->
    AlertDialog(
      onDismissRequest = { confirmation = null },
      title = { Text(stringResource(action.title)) },
      text = { Text(stringResource(action.message)) },
      confirmButton = {
        TextButton(
          enabled = enabled && (action != Confirmation.Overwrite || alreadyExists),
          onClick = {
            confirmation = null
            onDismiss()
            when (action) {
              Confirmation.Discard -> onDiscard(pending)
              Confirmation.Overwrite -> onOverwrite(pending)
            }
          },
        ) {
          Text(stringResource(action.label))
        }
      },
      dismissButton = {
        TextButton(onClick = { confirmation = null }) {
          Text(stringResource(Res.string.pending_bookmark_cancel))
        }
      },
    )
  }
}

@Composable
private fun ConflictActionLabel(icon: ImageVector, label: StringResource) {
  Row(
    horizontalArrangement = Arrangement.spacedBy(ButtonDefaults.IconSpacing),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(icon, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
    Text(stringResource(label))
  }
}

private enum class Confirmation(
  val title: StringResource,
  val message: StringResource,
  val label: StringResource,
) {
  Discard(
    Res.string.pending_bookmark_discard_title,
    Res.string.pending_bookmark_discard_message,
    Res.string.pending_bookmark_discard,
  ),
  Overwrite(
    Res.string.pending_bookmark_overwrite_title,
    Res.string.pending_bookmark_overwrite_message,
    Res.string.pending_bookmark_overwrite,
  ),
}
