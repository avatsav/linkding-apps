package dev.avatsav.linkding.api

import dev.avatsav.linkding.api.core.HttpClientFactory
import io.ktor.client.HttpClient


@LinkdingDsl
fun Linkding(block: LinkdingClientConfig.() -> Unit): Linkding {
    val config = LinkdingClientConfig().apply(block)
    return Linkding(config)
}

class Linkding internal constructor(config: LinkdingClientConfig) {
    init {
        requireNotNull(config.hostUrl) {
            "Linkding hostUrl is required. Set the hostUrl of the Linkding instance to connect to."
        }
        requireNotNull(config.apiKey) {
            "Linkding apiKey is required. Set the apiKey for the specified hostUrl"
        }
    }

    private val client: HttpClient = HttpClientFactory.buildHttpClient(config)

    val bookmarks: LinkdingBookmarksApi by buildApi(::DefaultLinkdingBookmarksApi)
    val tags: LinkdingTagsApi by buildApi(::DefaultLinkdingTagsApi)

    private inline fun <T> buildApi(crossinline builder: (HttpClient) -> T) = lazy {
        builder(client)
    }
}
