package dev.avatsav.linkding.data.bookmarks.mappers

import dev.avatsav.linkding.api.models.LinkdingCheckUrlResponse
import dev.avatsav.linkding.data.model.CheckUrlResult
import me.tatarka.inject.annotations.Inject

@Inject
class CheckUrlResultMapper {

    fun map(response: LinkdingCheckUrlResponse): CheckUrlResult {
        return CheckUrlResult(
            alreadyBookmarked = response.bookmark != null,
            url = response.metadata.url,
            title = response.metadata.title,
            description = response.metadata.description,
        )
    }
}
