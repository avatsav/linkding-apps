package dev.avatsav.linkding.api

import arrow.core.Either
import dev.avatsav.linkding.api.extensions.ApiResponse
import dev.avatsav.linkding.api.extensions.endpointTags
import dev.avatsav.linkding.api.extensions.get
import dev.avatsav.linkding.api.extensions.parameterPage
import dev.avatsav.linkding.api.extensions.parameterQuery
import dev.avatsav.linkding.api.extensions.toEither
import dev.avatsav.linkding.api.models.LinkdingErrorResponse
import dev.avatsav.linkding.api.models.LinkdingTagsResponse
import io.ktor.client.HttpClient

class DefaultLinkdingTagsApi(private val httpClient: HttpClient) : LinkdingTagsApi {
    override suspend fun getTags(
        offset: Int,
        limit: Int,
        query: String,
    ): Either<LinkdingErrorResponse, LinkdingTagsResponse> {
        val apiResponse: ApiResponse<LinkdingTagsResponse, LinkdingErrorResponse> = httpClient.get {
            endpointTags()
            parameterPage(offset, limit)
            parameterQuery(query)
        }
        return apiResponse.toEither(LinkdingErrorResponse.DEFAULT)
    }
}