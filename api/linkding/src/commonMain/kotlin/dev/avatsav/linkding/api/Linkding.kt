package dev.avatsav.linkding.api

import dev.avatsav.linkding.api.core.HttpClientFactory
import io.ktor.client.HttpClient

@LinkdingDsl
fun Linkding(
    apiConfig: LinkdingApiConfig,
    block: LinkdingClientConfig.() -> Unit,
): Linkding {
    val config = LinkdingClientConfig(apiConfig).apply(block)
    return Linkding(config)
}

class Linkding internal constructor(config: LinkdingClientConfig) {

    private val client: HttpClient = HttpClientFactory.buildHttpClient(config)

    val bookmarks: LinkdingBookmarksApi by buildApi(::DefaultLinkdingBookmarksApi)
    val tags: LinkdingTagsApi by buildApi(::DefaultLinkdingTagsApi)

    private inline fun <T> buildApi(crossinline builder: (HttpClient) -> T) = lazy {
        builder(client)
    }
}
