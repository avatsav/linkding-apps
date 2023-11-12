package dev.avatsav.linkding.api

import arrow.core.Either
import dev.avatsav.linkding.api.models.LinkdingErrorResponse
import dev.avatsav.linkding.api.models.LinkdingTagsResponse

interface LinkdingTagsApi {
    suspend fun getTags(
        offset: Int,
        limit: Int,
        query: String,
    ): Either<LinkdingErrorResponse, LinkdingTagsResponse>

}
