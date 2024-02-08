package dev.avatsav.linkding.api

import dev.avatsav.linkding.api.core.HttpClientFactory
import io.ktor.client.HttpClient

@LinkdingDsl
fun Linkding(
    apiConfig: LinkdingApiConfig,
    block: LinkdingClientConfig.() -> Unit,
): Linkding {
    val clientConfig = LinkdingClientConfig().apply(block)
    return Linkding(apiConfig, clientConfig)
}

class Linkding internal constructor(
    apiConfig: LinkdingApiConfig,
    clientConfig: LinkdingClientConfig,
) {

    private val client: HttpClient = HttpClientFactory.buildHttpClient(clientConfig, apiConfig)

    val bookmarks: LinkdingBookmarksApi by buildApi(::DefaultLinkdingBookmarksApi)
    val tags: LinkdingTagsApi by buildApi(::DefaultLinkdingTagsApi)

    private inline fun <T> buildApi(crossinline builder: (HttpClient) -> T) = lazy {
        builder(client)
    }
}
