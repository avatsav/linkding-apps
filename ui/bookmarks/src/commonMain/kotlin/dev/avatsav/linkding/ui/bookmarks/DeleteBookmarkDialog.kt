package dev.avatsav.linkding.ui.bookmarks

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import dev.avatsav.linkding.data.model.Bookmark

@Composable
fun DeleteBookmarkDialog(
    bookmark: Bookmark,
    onDismissRequest: () -> Unit,
    onConfirm: (Bookmark) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        icon = { Icons.Rounded.Delete },
        title = {
            Text(text = "Delete Bookmark?")
        },
        text = {
            Text("Are you sure you want to delete \n\"${bookmark.title}\"?")
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(bookmark) }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismissRequest() }) {
                Text("Dismiss")
            }
        },
    )
}
