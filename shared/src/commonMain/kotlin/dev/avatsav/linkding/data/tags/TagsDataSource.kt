package dev.avatsav.linkding.data.tags

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import dev.avatsav.linkding.domain.LinkdingErrorResponse
import dev.avatsav.linkding.domain.TagError
import dev.avatsav.linkding.domain.TagList
import dev.avatsav.linkding.extensions.ApiResponse
import dev.avatsav.linkding.extensions.requestApiResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.headers
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.Url
import io.ktor.http.contentType
import io.ktor.http.encodedPath

interface TagsDataSource {

    suspend fun fetch(
        baseUrl: String,
        token: String,
        offset: Int,
        limit: Int,
        query: String,
    ): Either<TagError, TagList>
}

internal class LinkdingTagsDataSource(private val httpClient: HttpClient) : TagsDataSource {

    override suspend fun fetch(
        baseUrl: String,
        token: String,
        offset: Int,
        limit: Int,
        query: String,
    ): Either<TagError, TagList> {
        val apiResponse: ApiResponse<TagList, LinkdingErrorResponse> =
            httpClient.requestApiResponse {
                method = HttpMethod.Get
                headers {
                    append(HttpHeaders.Authorization, "Token $token")
                }
                url {
                    val url = Url(baseUrl)
                    protocol = url.protocol
                    host = url.host
                    encodedPath = "api/tags/"
                    parameters.append("offset", offset.toString())
                    parameters.append("limit", limit.toString())
                    if (query.isNotEmpty()) {
                        parameters.append("q", query)
                    }
                }
                contentType(ContentType.Application.Json)
            }
        return when (apiResponse) {
            is ApiResponse.Success -> return apiResponse.body.right()
            is ApiResponse.Error -> {
                val errorMessage =
                    apiResponse.body?.detail ?: apiResponse.message ?: "No error message"
                TagError.CouldNotGetTag(errorMessage).left()
            }
        }
    }
}
