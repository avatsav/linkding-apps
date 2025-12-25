package dev.avatsav.linkding.bookmarks.impl.mappers

import dev.avatsav.linkding.api.models.LinkdingCheckUrlResponse
import dev.avatsav.linkding.data.model.CheckUrlResult
import dev.zacsweers.metro.Inject

@Inject
class CheckUrlResultMapper(private val bookmarkMapper: BookmarkMapper) {

  fun map(response: LinkdingCheckUrlResponse): CheckUrlResult =
    CheckUrlResult(
      existingBookmark = response.bookmark?.let { bookmarkMapper.map(it) },
      url = response.metadata.url,
      title = response.metadata.title,
      description = response.metadata.description,
    )
}
