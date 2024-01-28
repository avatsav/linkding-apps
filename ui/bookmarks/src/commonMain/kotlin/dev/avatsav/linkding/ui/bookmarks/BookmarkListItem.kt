//package dev.avatsav.linkding.ui.bookmarks
//
//import android.content.res.Configuration
//import androidx.compose.animation.core.Animatable
//import androidx.compose.animation.core.Spring
//import androidx.compose.animation.core.spring
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.BoxScope
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.lazy.LazyRow
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.DismissState
//import androidx.compose.material3.Divider
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Icon
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.scale
//import androidx.compose.ui.graphics.painter.Painter
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import dev.avatsav.linkding.android.R
//import dev.avatsav.linkding.android.theme.LinkdingTheme
//import dev.avatsav.linkding.android.ui.common.SwipeAction
//import dev.avatsav.linkding.android.ui.common.SwipeableListItem
//import dev.avatsav.linkding.ui.bookmarks.BookmarkViewItem
//import dev.avatsav.linkding.ui.bookmarks.vm.BookmarkViewItem
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun BookmarkListItem(
//    modifier: Modifier = Modifier,
//    bookmark: BookmarkViewItem,
//    openBookmark: (BookmarkViewItem) -> Unit,
//    toggleArchive: (BookmarkViewItem, DismissState) -> Unit,
//    deleteBookmark: (BookmarkViewItem, DismissState) -> Unit,
//) {
//    var dismissState: DismissState? = null
//
//    Column(modifier = modifier) {
//        SwipeableListItem(
//            modifier = Modifier,
//            background = MaterialTheme.colorScheme.primaryContainer,
//            startAction = SwipeAction(
//                onSwipe = { dismissState?.let { deleteBookmark(bookmark, it) } },
//                background = MaterialTheme.colorScheme.primary,
//                canDismiss = true,
//                content = { dismissing ->
//                    BookmarkSwipeActionContent(
//                        painter = painterResource(R.drawable.delete_24),
//                        text = "Delete",
//                        dismissing = dismissing,
//                    )
//                },
//            ),
//            endAction = SwipeAction(
//                onSwipe = { dismissState?.let { toggleArchive(bookmark, it) } },
//                background = MaterialTheme.colorScheme.primary,
//                canDismiss = true,
//                content = { dismissing ->
//                    BookmarkSwipeActionContent(
//                        painter = painterResource(
//                            if (bookmark.archived) {
//                                R.drawable.unarchive_24
//                            } else {
//                                R.drawable.archive_24
//                            },
//                        ),
//                        text = if (bookmark.archived) "Unarchive" else "Archive",
//                        dismissing = dismissing,
//                    )
//                },
//            ),
//        ) {
//            dismissState = it
//            BookmarkContent(
//                modifier = Modifier.clickable { openBookmark(bookmark) },
//                bookmark = bookmark,
//            )
//        }
//        Divider(thickness = 0.5.dp)
//    }
//}
//
//@Composable
//private fun BoxScope.BookmarkSwipeActionContent(
//    painter: Painter,
//    text: String,
//    dismissing: Boolean,
//) {
//    val iconAnimatable = remember { Animatable(if (dismissing) .7f else 1f) }
//    LaunchedEffect(
//        key1 = Unit,
//        block = {
//            if (dismissing) {
//                iconAnimatable.snapTo(.7f)
//                iconAnimatable.animateTo(1f, spring(Spring.DampingRatioHighBouncy))
//            }
//        },
//    )
//    Column(
//        modifier = Modifier
//            .padding(horizontal = 20.dp)
//            .align(Alignment.Center),
//        verticalArrangement = Arrangement.spacedBy(4.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//    ) {
//        Icon(
//            modifier = Modifier
//                .scale(iconAnimatable.value)
//                .size(30.dp),
//            painter = painter,
//            tint = if (dismissing) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
//            contentDescription = text,
//        )
//        Text(
//            text = text,
//            style = MaterialTheme.typography.bodySmall,
//            color = if (dismissing) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
//        )
//    }
//}
//
//@Composable
//private fun BookmarkContent(
//    modifier: Modifier = Modifier,
//    bookmark: BookmarkViewItem,
//) {
//    Column(
//        modifier = modifier
//            .background(MaterialTheme.colorScheme.surface)
//            .padding(16.dp)
//            .fillMaxWidth(),
//        verticalArrangement = Arrangement.spacedBy(8.dp),
//    ) {
//        Text(
//            text = bookmark.title,
//            color = MaterialTheme.colorScheme.primary,
//            maxLines = 2,
//            overflow = TextOverflow.Ellipsis,
//        )
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.spacedBy(4.dp),
//        ) {
//            Icon(
//                painter = painterResource(id = R.drawable.link_12),
//                contentDescription = bookmark.title + "link",
//            )
//            Text(
//                text = bookmark.urlHostName,
//                style = MaterialTheme.typography.bodySmall,
//                color = MaterialTheme.colorScheme.onSurfaceVariant,
//                maxLines = 1,
//                overflow = TextOverflow.Ellipsis,
//            )
//        }
//        if (bookmark.description.isNotEmpty()) {
//            Text(
//                text = bookmark.description,
//                style = MaterialTheme.typography.bodyMedium,
//                maxLines = 3,
//                overflow = TextOverflow.Ellipsis,
//            )
//        }
//        LazyRow(
//            horizontalArrangement = Arrangement.spacedBy(4.dp),
//        ) {
//            items(items = bookmark.tagNames.toTypedArray()) { tagName ->
//                Text(
//                    text = "#$tagName",
//                    color = MaterialTheme.colorScheme.tertiary,
//                    style = MaterialTheme.typography.labelMedium,
//                )
//            }
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Preview(showBackground = true)
//@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
//@Composable
//fun BookmarkItem_Preview() {
//    LinkdingTheme {
//        Surface {
//            BookmarkListItem(
//                bookmark = BookmarkViewItem(
//                    id = 1L,
//                    title = "Effective Null Checks in Java",
//                    description = "Strategies to avoid bike-shedding and get on with Java code-reviews with confidence ",
//                    url = "https://www.blog.com/effective-nulls-java",
//                    urlHostName = "https://www.blog.com",
//                    archived = false,
//                    tagNames = setOf("java", "null checks", "kotlin", "blogpost"),
//                ),
//                openBookmark = {},
//                toggleArchive = { _, _ -> },
//                deleteBookmark = { _, _ -> },
//            )
//        }
//    }
//}
//
//@Preview(showBackground = true)
//@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
//@Composable
//fun BookmarkSwipeActionContent_Preview() {
//    LinkdingTheme {
//        Surface {
//            Box {
//                BookmarkSwipeActionContent(
//                    painter = painterResource(R.drawable.archive_24),
//                    text = "Archive",
//                    dismissing = false,
//                )
//            }
//        }
//    }
//}
