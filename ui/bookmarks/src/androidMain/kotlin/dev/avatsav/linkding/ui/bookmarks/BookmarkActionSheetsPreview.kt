package dev.avatsav.linkding.ui.bookmarks

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.ui.theme.LinkdingTheme

private val previewBookmark = Bookmark(
    id = 1L,
    externalId = 2L,
    url = "https://newsletter.bijanstephen.blog/lateral-thinking-with-withered-technology/",
    urlHost = "https://newsletter.bijanstephen.blog",
    title = "Lateral thinking with withered technology",
    description = "I've been thinking about Gunpei Yokoi a lot lately. Before his tragic death in 1997, Yokoi was a game designer at Nintendo — one of the first, in fact. For them, he created the Game & Watch, the Virtual Boy, and the original Game Boy",
)

@Preview(showBackground = true)
@Composable
fun DeleteActionSheetPreview() {
    LinkdingTheme {
        DeleteBookmarkActionSheet(
            bookmark = previewBookmark,
            onConfirm = {},
            onCancelled = {},
        )
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Composable
fun ArchiveActionSheetPreview() {
    LinkdingTheme {
        ArchiveBookmarkActionSheet(
            bookmark = previewBookmark,
            onConfirm = {},
            onCancelled = {},
        )
    }
}
