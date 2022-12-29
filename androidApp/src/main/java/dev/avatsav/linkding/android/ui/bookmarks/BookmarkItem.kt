@file:OptIn(ExperimentalMaterial3Api::class)

package dev.avatsav.linkding.android.ui.bookmarks

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.avatsav.linkding.android.R
import dev.avatsav.linkding.android.ui.theme.LinkdingTheme
import dev.avatsav.linkding.ui.model.BookmarkViewItem


@Composable
fun BookmarkItem(
    modifier: Modifier = Modifier,
    bookmark: BookmarkViewItem,
    onClicked: (BookmarkViewItem) -> Unit,
    onTagClicked: (String) -> Unit,
) {
    Column(modifier = modifier
        .clickable { onClicked(bookmark) }
        .padding(16.dp)
        .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_link_12),
                contentDescription = bookmark.title + "link"
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
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(items = bookmark.tagNames.toTypedArray()) { tagName ->
                Text(
                    modifier = Modifier.clickable { /*TODO*/ },
                    text = "#$tagName",
                    color = MaterialTheme.colorScheme.tertiary,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun BookmarkItem_Preview() {
    LinkdingTheme {
        Surface {
            BookmarkItem(bookmark = BookmarkViewItem(
                id = 1L,
                title = "Effective Null Checks in Java",
                description = "Strategies to avoid bike-shedding and get on with Java code-reviews with confidence ",
                url = "https://www.blog.com/effective-nulls-java",
                urlHostName = "https://www.blog.com",
                tagNames = setOf("java", "null checks", "kotlin", "blogpost")
            ), onClicked = {}, onTagClicked = {})
        }
    }
}