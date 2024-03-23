package dev.avatsav.linkding.data.bookmarks.mappers

import dev.avatsav.linkding.api.models.LinkdingTag
import dev.avatsav.linkding.api.models.LinkdingTagsResponse
import dev.avatsav.linkding.data.model.Tag
import dev.avatsav.linkding.data.model.TagsResult
import me.tatarka.inject.annotations.Inject

@Inject
class TagMapper {

    fun map(response: LinkdingTagsResponse): TagsResult {
        return TagsResult(
            tags = response.results.map { map(it) },
            nextPage = response.next,
            previousPage = response.previous,
        )
    }

    fun map(tag: LinkdingTag): Tag {
        return Tag(tag.id, tag.name)
    }
}
