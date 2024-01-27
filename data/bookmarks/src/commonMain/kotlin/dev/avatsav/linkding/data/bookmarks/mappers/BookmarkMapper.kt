package dev.avatsav.linkding.data.bookmarks.mappers

import dev.avatsav.linkding.api.models.LinkdingBookmark
import dev.avatsav.linkding.api.models.LinkdingBookmarksResponse
import dev.avatsav.linkding.api.models.LinkdingSaveBookmarkRequest
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.data.model.SaveBookmark

class BookmarkMapper {

    fun map(response: LinkdingBookmarksResponse): List<Bookmark> {
        return response.results.map { map(it) }
    }

    fun map(linkdingBookmark: LinkdingBookmark) =
        Bookmark(
            id = linkdingBookmark.id,
            externalId = linkdingBookmark.id,
            url = linkdingBookmark.url,
            title = linkdingBookmark.getSafeTitle(),
            description = linkdingBookmark.getSafeDescription(),
            archived = linkdingBookmark.isArchived,
            unread = linkdingBookmark.unread,
            tags = linkdingBookmark.tagNames,
            added = linkdingBookmark.dateAdded,
            modified = linkdingBookmark.dateModified,
        )

    fun map(saveBookmark: SaveBookmark) = LinkdingSaveBookmarkRequest(
        url = saveBookmark.url,
        title = saveBookmark.title,
        description = saveBookmark.description,
        tagNames = saveBookmark.tags,
        isArchived = saveBookmark.archived,
        unread = saveBookmark.unread,
        shared = saveBookmark.shared,
    )
}

