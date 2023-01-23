@file:OptIn(ExperimentalMaterial3Api::class)

package dev.avatsav.linkding.android.ui.bookmarks

import android.content.res.Configuration
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
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.avatsav.linkding.android.R
import dev.avatsav.linkding.android.theme.LinkdingTheme
import dev.avatsav.linkding.android.ui.common.SwipeAction
import dev.avatsav.linkding.android.ui.common.SwipeableListItem
import dev.avatsav.linkding.ui.bookmarks.BookmarkViewItem

@Composable
fun BookmarkListItem(
    modifier: Modifier = Modifier,
    bookmark: BookmarkViewItem,
    openBookmark: (BookmarkViewItem) -> Unit,
    toggleArchive: (Long) -> Unit,
    toggleUnread: (Long) -> Unit,
) {
    SwipeableListItem(
        modifier = modifier,
        startAction = SwipeAction(
            onSwipe = { toggleUnread(bookmark.id) },
            background = MaterialTheme.colorScheme.primary,
            canDismiss = false,
            content = { dismissing ->
                BookmarkSwipeActionContent(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Unread",
                    dismissing = dismissing,
                )
            },
        ),
        endAction = SwipeAction(
            onSwipe = { toggleArchive(bookmark.id) },
            background = MaterialTheme.colorScheme.primary,
            canDismiss = false,
            content = { dismissing ->
                BookmarkSwipeActionContent(
                    imageVector = Icons.Default.MailOutline,
                    contentDescription = "Archive",
                    dismissing = dismissing,
                )
            },
        ),
    ) {
        BookmarkContent(
            modifier = Modifier.clickable { openBookmark(bookmark) },
            bookmark = bookmark,
        )
    }
}

@Composable
private fun BoxScope.BookmarkSwipeActionContent(

    imageVector: ImageVector,
    contentDescription: String,
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
    Icon(
        modifier = Modifier
            .padding(24.dp)
            .align(Alignment.Center)
            .scale(iconAnimatable.value)
            .size(30.dp),
        imageVector = imageVector,
        tint = if (dismissing) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
        contentDescription = contentDescription,
    )
}

@Composable
private fun BookmarkContent(
    modifier: Modifier = Modifier,
    bookmark: BookmarkViewItem,
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
        if (bookmark.description.isNotEmpty()) {
            Text(
                text = bookmark.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_link_12),
                contentDescription = bookmark.title + "link",
            )
            Text(
                text = bookmark.urlHostName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(items = bookmark.tagNames.toTypedArray()) { tagName ->
                Text(
                    text = "#$tagName",
                    color = MaterialTheme.colorScheme.tertiary,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
    Divider(thickness = 0.5.dp)
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun BookmarkItem_Preview() {
    LinkdingTheme {
        Surface {
            BookmarkListItem(
                bookmark = BookmarkViewItem(
                    id = 1L,
                    title = "Effective Null Checks in Java",
                    description = "Strategies to avoid bike-shedding and get on with Java code-reviews with confidence ",
                    url = "https://www.blog.com/effective-nulls-java",
                    urlHostName = "https://www.blog.com",
                    tagNames = setOf("java", "null checks", "kotlin", "blogpost"),
                ),
                openBookmark = {},
                toggleArchive = {},
                toggleUnread = {},
            )
        }
    }
}
