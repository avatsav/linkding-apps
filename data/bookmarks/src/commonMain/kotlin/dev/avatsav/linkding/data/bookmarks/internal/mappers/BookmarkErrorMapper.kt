package dev.avatsav.linkding.data.bookmarks.internal.mappers

import dev.avatsav.linkding.api.models.LinkdingError
import dev.avatsav.linkding.data.model.BookmarkError
import me.tatarka.inject.annotations.Inject

@Inject
class BookmarkErrorMapper {
  fun map(error: LinkdingError) = BookmarkError(error.message)
}
