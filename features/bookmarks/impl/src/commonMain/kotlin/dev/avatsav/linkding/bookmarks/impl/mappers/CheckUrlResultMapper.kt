package dev.avatsav.linkding.bookmarks.impl.mappers

import dev.avatsav.linkding.api.models.LinkdingCheckUrlResponse
import dev.avatsav.linkding.data.model.CheckUrlResult
import dev.zacsweers.metro.Inject

@Inject
class CheckUrlResultMapper {

  fun map(response: LinkdingCheckUrlResponse): CheckUrlResult =
    CheckUrlResult(
      alreadyBookmarked = response.bookmark != null,
      url = response.metadata.url,
      title = response.metadata.title,
      description = response.metadata.description,
    )
}
