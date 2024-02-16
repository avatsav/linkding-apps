package dev.avatsav.linkding.api

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.map
import dev.avatsav.linkding.api.core.HttpClientFactory
import dev.avatsav.linkding.api.extensions.endpointBookmarks
import dev.avatsav.linkding.api.extensions.get
import dev.avatsav.linkding.api.extensions.parameterPage
import dev.avatsav.linkding.api.extensions.toLinkdingResult
import dev.avatsav.linkding.api.models.LinkdingBookmarksResponse
import dev.avatsav.linkding.api.models.LinkdingError
import dev.avatsav.linkding.api.models.LinkdingErrorResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.Url
import io.ktor.http.takeFrom

@LinkdingDsl
fun LinkdingConnectionApi(
    block: LinkdingClientConfig.() -> Unit,
): LinkdingConnectionApi {
    val clientConfig = LinkdingClientConfig().apply(block)
    return LinkdingConnectionApi(clientConfig)
}

class LinkdingConnectionApi internal constructor(clientConfig: LinkdingClientConfig) {

    private val httpClient: HttpClient = HttpClientFactory.buildHttpClient(clientConfig)

    suspend fun connect(
        hostUrl: LinkdingHostUrl,
        apiKey: LinkdingApiKey,
    ): Result<Unit, LinkdingError> {
        return httpClient.get<LinkdingBookmarksResponse, LinkdingErrorResponse> {
            header(HttpHeaders.Authorization, "Token $apiKey")
            url {
                takeFrom(Url(hostUrl))
            }
            endpointBookmarks()
            parameterPage(0, 1)
        }.toLinkdingResult().map { }
    }
}
