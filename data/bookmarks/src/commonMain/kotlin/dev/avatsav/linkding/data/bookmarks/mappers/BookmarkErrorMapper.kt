package dev.avatsav.linkding.data.bookmarks.mappers

import dev.avatsav.linkding.api.models.LinkdingErrorResponse
import dev.avatsav.linkding.data.model.BookmarkError

class BookmarkErrorMapper {

    fun map(linkdingBookmarkError: LinkdingErrorResponse) =
        BookmarkError(linkdingBookmarkError.detail)

}
