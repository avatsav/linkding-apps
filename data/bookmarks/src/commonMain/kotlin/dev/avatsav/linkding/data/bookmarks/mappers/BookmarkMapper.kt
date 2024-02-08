package dev.avatsav.linkding.data.bookmarks.mappers

import dev.avatsav.linkding.api.models.LinkdingBookmark
import dev.avatsav.linkding.api.models.LinkdingBookmarksResponse
import dev.avatsav.linkding.api.models.LinkdingSaveBookmarkRequest
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.data.model.BookmarksResult
import dev.avatsav.linkding.data.model.SaveBookmark
import io.ktor.http.Url
import me.tatarka.inject.annotations.Inject

@Inject
class BookmarkMapper {

    fun map(response: LinkdingBookmarksResponse): BookmarksResult {
        return BookmarksResult(
            bookmarks = response.results.map { map(it) },
            previousPage = response.previous,
            nextPage = response.next,
        )
    }

    fun map(linkdingBookmark: LinkdingBookmark) =
        Bookmark(
            id = linkdingBookmark.id,
            externalId = linkdingBookmark.id,
            url = linkdingBookmark.url,
            urlHost = Url(linkdingBookmark.url).host,
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
