package dev.avatsav.linkding.ui.bookmarks.widgets

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Unarchive
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.ui.shared.SwipeAction
import dev.avatsav.linkding.ui.shared.SwipeableListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarkListItem(
    bookmark: Bookmark,
    openBookmark: (Bookmark) -> Unit,
    toggleArchive: (Bookmark, SwipeToDismissBoxState) -> Unit,
    deleteBookmark: (Bookmark, SwipeToDismissBoxState) -> Unit,
    modifier: Modifier = Modifier,
) {
    var dismissState: SwipeToDismissBoxState? = null

    Column(modifier = modifier) {
        SwipeableListItem(
            modifier = Modifier,
            background = MaterialTheme.colorScheme.primaryContainer,
            startAction = SwipeAction(
                onSwipe = { dismissState?.let { deleteBookmark(bookmark, it) } },
                background = MaterialTheme.colorScheme.primary,
                canDismiss = true,
                content = { dismissing ->
                    BookmarkSwipeActionContent(
                        imageVector = Icons.Default.Delete,
                        text = "Delete",
                        dismissing = dismissing,
                    )
                },
            ),
            endAction = SwipeAction(
                onSwipe = { dismissState?.let { toggleArchive(bookmark, it) } },
                background = MaterialTheme.colorScheme.primary,
                canDismiss = true,
                content = { dismissing ->
                    BookmarkSwipeActionContent(
                        imageVector = if (bookmark.archived) Icons.Default.Unarchive else Icons.Default.Archive,
                        text = if (bookmark.archived) "Unarchive" else "Archive",
                        dismissing = dismissing,
                    )
                },
            ),
        ) {
            dismissState = it
            BookmarkContent(
                modifier = Modifier.clickable { openBookmark(bookmark) },
                bookmark = bookmark,
            )
        }
        HorizontalDivider(thickness = 0.5.dp)
    }
}

@Composable
private fun BoxScope.BookmarkSwipeActionContent(
    imageVector: ImageVector,
    text: String,
    dismissing: Boolean,
) {
    val iconAnimatable = remember { Animatable(if (dismissing) .7f else 1f) }
    LaunchedEffect(
        key1 = Unit,
        block = {
            if (dismissing) {
                iconAnimatable.snapTo(.7f)
                iconAnimatable.animateTo(1f, spring(Spring.DampingRatioHighBouncy))
            }
        },
    )
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .align(Alignment.Center),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            modifier = Modifier
                .scale(iconAnimatable.value)
                .size(30.dp),
            imageVector = imageVector,
            tint = if (dismissing) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
            contentDescription = text,
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = if (dismissing) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun BookmarkContent(
    modifier: Modifier = Modifier,
    bookmark: Bookmark,
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = bookmark.title,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
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
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(items = bookmark.tags.toTypedArray()) { tagName ->
                Text(
                    text = "#$tagName",
                    color = MaterialTheme.colorScheme.tertiary,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}