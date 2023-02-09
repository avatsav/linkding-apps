package dev.avatsav.linkding.android.ui.bookmarks

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import dev.avatsav.linkding.android.R
import dev.avatsav.linkding.ui.bookmarks.BookmarkViewItem

@Composable
fun DeleteBookmarkDialog(
    bookmark: BookmarkViewItem,
    onDismissRequest: () -> Unit,
    onConfirm: (BookmarkViewItem) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        icon = { Icon(painterResource(id = R.drawable.delete_24), contentDescription = "Delete") },
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
