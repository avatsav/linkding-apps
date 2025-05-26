package dev.avatsav.linkding.bookmarks.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Unarchive
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.slack.circuit.overlay.OverlayHost
import dev.avatsav.linkding.bookmarks.ui.list.widgets.BookmarkContent
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.ui.circuit.BottomSheetOverlay

@OptIn(ExperimentalMaterial3Api::class)
suspend fun OverlayHost.showDeleteBookmarkAction(bookmark: Bookmark): ActionResult =
  show(
    BottomSheetOverlay(model = bookmark, onDismiss = { ActionResult.Dismissed }) {
      toDelete,
      overlayNavigator ->
      DeleteBookmarkActionSheet(
        bookmark = toDelete,
        onConfirm = { overlayNavigator.finish(ActionResult.Confirmed) },
        onCancel = { overlayNavigator.finish(ActionResult.Cancelled) },
      )
    }
  )

@OptIn(ExperimentalMaterial3Api::class)
suspend fun OverlayHost.showArchiveBookmarkAction(bookmark: Bookmark): ActionResult =
  show(
    BottomSheetOverlay(model = bookmark, onDismiss = { ActionResult.Dismissed }) {
      toArchive,
      overlayNavigator ->
      ArchiveBookmarkActionSheet(
        bookmark = toArchive,
        onConfirm = { overlayNavigator.finish(ActionResult.Confirmed) },
        onCancel = { overlayNavigator.finish(ActionResult.Cancelled) },
      )
    }
  )

enum class ActionResult {
  Confirmed,
  Cancelled,
  Dismissed,
}

@Composable
fun DeleteBookmarkActionSheet(bookmark: Bookmark, onConfirm: () -> Unit, onCancel: () -> Unit) {
  BookmarkActionSheet(
    titleText = "Delete Bookmark?",
    imageVector = Icons.Default.Delete,
    bookmark = bookmark,
    onConfirm = onConfirm,
    onCancel = onCancel,
  )
}

@Composable
fun ArchiveBookmarkActionSheet(bookmark: Bookmark, onConfirm: () -> Unit, onCancel: () -> Unit) {
  BookmarkActionSheet(
    titleText = if (bookmark.archived) "Unarchive Bookmark?" else "Archive Bookmark?",
    imageVector = if (bookmark.archived) Icons.Default.Unarchive else Icons.Default.Archive,
    bookmark = bookmark,
    onConfirm = onConfirm,
    onCancel = onCancel,
  )
}

@Composable
fun BookmarkActionSheet(
  titleText: String,
  imageVector: ImageVector,
  bookmark: Bookmark,
  onConfirm: () -> Unit,
  onCancel: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 48.dp)) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
      Text(text = titleText, style = MaterialTheme.typography.titleLarge)
      Icon(
        imageVector = imageVector,
        contentDescription = "",
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.size(24.dp),
      )
    }
    Spacer(modifier = Modifier.size(24.dp))
    Card { BookmarkContent(bookmark = bookmark) }
    Spacer(modifier = Modifier.size(24.dp))
    Row(
      modifier = Modifier.align(Alignment.End),
      horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      OutlinedButton(onClick = { onCancel() }) { Text("Cancel") }
      Button(onClick = { onConfirm() }) { Text("Confirm") }
    }
  }
}
