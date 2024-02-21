package dev.avatsav.linkding.data.bookmarks.mappers

import dev.avatsav.linkding.api.models.LinkdingBookmarkCategory
import dev.avatsav.linkding.data.model.BookmarkCategory

fun BookmarkCategory.toLinkding(): LinkdingBookmarkCategory = when (this) {
    BookmarkCategory.All -> LinkdingBookmarkCategory.All
    BookmarkCategory.Archived -> LinkdingBookmarkCategory.Archived
    BookmarkCategory.Unread -> LinkdingBookmarkCategory.Unread
    BookmarkCategory.Untagged -> LinkdingBookmarkCategory.Untagged
}
