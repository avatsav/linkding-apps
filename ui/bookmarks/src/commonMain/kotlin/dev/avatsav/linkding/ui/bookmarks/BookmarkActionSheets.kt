package dev.avatsav.linkding.ui.bookmarks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.ui.bookmarks.widgets.BookmarkContent
import dev.avatsav.linkding.ui.circuit.BottomSheetOverlay

suspend fun OverlayHost.showDeleteBookmarkAction(bookmark: Bookmark): ActionResult {
    return show(
        BottomSheetOverlay(
            model = bookmark,
            sheetShape = RoundedCornerShape(topEnd = 16.dp, topStart = 16.dp),
            onDismiss = { ActionResult.Dismissed },
        ) { toDelete, overlayNavigator ->
            DeleteBookmarkActionSheet(
                bookmark = toDelete,
                onConfirm = { overlayNavigator.finish(ActionResult.Confirmed) },
                onCancelled = { overlayNavigator.finish(ActionResult.Cancelled) },
            )
        },
    )
}

suspend fun OverlayHost.showArchiveBookmarkAction(bookmark: Bookmark): ActionResult {
    return show(
        BottomSheetOverlay(
            model = bookmark,
            onDismiss = { ActionResult.Dismissed },
        ) { toArchive, overlayNavigator ->
            ArchiveBookmarkActionSheet(
                bookmark = toArchive,
                onConfirm = { overlayNavigator.finish(ActionResult.Confirmed) },
                onCancelled = { overlayNavigator.finish(ActionResult.Cancelled) },
            )
        },
    )
}

enum class ActionResult {
    Confirmed,
    Cancelled,
    Dismissed,
}

@Composable
fun DeleteBookmarkActionSheet(
    bookmark: Bookmark,
    onConfirm: () -> Unit,
    onCancelled: () -> Unit,
) {
    BookmarkActionSheet(
        titleText = "Delete Bookmark?",
        imageVector = Icons.Default.DeleteSweep,
        bookmark = bookmark,
        onConfirm = onConfirm,
        onCancelled = onCancelled,
    )
}

@Composable
fun ArchiveBookmarkActionSheet(
    bookmark: Bookmark,
    onConfirm: () -> Unit,
    onCancelled: () -> Unit,
) {
    BookmarkActionSheet(
        titleText = "Archive Bookmark?",
        imageVector = Icons.Default.Archive,
        bookmark = bookmark,
        onConfirm = onConfirm,
        onCancelled = onCancelled,
    )
}

@Composable
fun BookmarkActionSheet(
    titleText: String,
    imageVector: ImageVector,
    bookmark: Bookmark,
    onConfirm: () -> Unit,
    onCancelled: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(
            start = 16.dp,
            end = 16.dp,
            top = 12.dp,
            bottom = 48.dp,
        ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = titleText,
                style = MaterialTheme.typography.titleLarge,
            )
            Icon(
                imageVector = imageVector,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp),
            )
        }
        Spacer(modifier = Modifier.size(24.dp))
        Card {
            BookmarkContent(bookmark = bookmark)
        }
        Spacer(modifier = Modifier.size(24.dp))
        Row(
            modifier = Modifier.align(Alignment.End),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OutlinedButton(onClick = { onCancelled() }) {
                Text("Cancel")
            }
            Button(onClick = { onConfirm() }) {
                Text("Confirm")
            }
        }
    }
}
