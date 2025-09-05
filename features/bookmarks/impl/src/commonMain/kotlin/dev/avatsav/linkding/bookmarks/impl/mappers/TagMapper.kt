package dev.avatsav.linkding.bookmarks.impl.mappers

import dev.avatsav.linkding.api.models.LinkdingTag
import dev.avatsav.linkding.api.models.LinkdingTagsResponse
import dev.avatsav.linkding.data.model.Tag
import dev.avatsav.linkding.data.model.TagsResult
import dev.zacsweers.metro.Inject

@Inject
class TagMapper {

  fun map(response: LinkdingTagsResponse): TagsResult =
    TagsResult(
      tags = response.results.map { map(it) },
      nextPage = response.next,
      previousPage = response.previous,
    )

  fun map(tag: LinkdingTag): Tag = Tag(tag.id, tag.name)
}
