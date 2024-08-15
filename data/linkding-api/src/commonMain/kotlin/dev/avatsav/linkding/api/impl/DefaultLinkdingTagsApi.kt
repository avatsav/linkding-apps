package dev.avatsav.linkding.api.impl

import com.github.michaelbull.result.Result
import dev.avatsav.linkding.api.LinkdingTagsApi
import dev.avatsav.linkding.api.extensions.endpointTags
import dev.avatsav.linkding.api.extensions.get
import dev.avatsav.linkding.api.extensions.parameterPage
import dev.avatsav.linkding.api.extensions.parameterQuery
import dev.avatsav.linkding.api.extensions.toLinkdingResult
import dev.avatsav.linkding.api.models.LinkdingError
import dev.avatsav.linkding.api.models.LinkdingErrorResponse
import dev.avatsav.linkding.api.models.LinkdingTagsResponse
import io.ktor.client.HttpClient

internal class DefaultLinkdingTagsApi(private val httpClient: HttpClient) : LinkdingTagsApi {
    override suspend fun getTags(
        offset: Int,
        limit: Int,
        query: String,
    ): Result<LinkdingTagsResponse, LinkdingError> =
        httpClient.get<LinkdingTagsResponse, LinkdingErrorResponse> {
            endpointTags()
            parameterPage(offset, limit)
            parameterQuery(query)
        }.toLinkdingResult()
}
