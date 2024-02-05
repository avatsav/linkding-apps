package dev.avatsav.linkding.api

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.map
import dev.avatsav.linkding.api.core.HttpClientFactory
import dev.avatsav.linkding.api.extensions.endpointBookmarks
import dev.avatsav.linkding.api.extensions.get
import dev.avatsav.linkding.api.extensions.parameterPage
import dev.avatsav.linkding.api.extensions.toResult
import dev.avatsav.linkding.api.models.LinkdingBookmarksResponse
import dev.avatsav.linkding.api.models.LinkdingErrorResponse
import io.ktor.client.HttpClient

@LinkdingDsl
fun LinkdingConnectionTester(
    block: LinkdingClientConfig.() -> Unit,
): LinkdingConnectionTester {
    return LinkdingConnectionTester(block)
}

class LinkdingConnectionTester internal constructor(clientConfig: LinkdingClientConfig) {

    private val httpClient: HttpClient = HttpClientFactory.buildHttpClient(clientConfig)
    suspend fun test(
        hostUrl: LinkdingHostUrl,
        apiKey: LinkdingApiKey,
    ): Result<Unit, LinkdingErrorResponse> {
        return httpClient.get<LinkdingBookmarksResponse, LinkdingErrorResponse> {
            endpointBookmarks()
            parameterPage(0, 1)
        }.toResult(LinkdingErrorResponse.DEFAULT).map { }
    }
}
