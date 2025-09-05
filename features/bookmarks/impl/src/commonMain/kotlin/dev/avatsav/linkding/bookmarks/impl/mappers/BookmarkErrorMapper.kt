package dev.avatsav.linkding.bookmarks.impl.mappers

import dev.avatsav.linkding.api.models.LinkdingError
import dev.avatsav.linkding.data.model.BookmarkError
import dev.zacsweers.metro.Inject

@Inject
class BookmarkErrorMapper {
  fun map(error: LinkdingError) = BookmarkError(error.message)
}
