package dev.avatsav.linkding.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.logging.Logging

typealias LinkdingHostUrl = String
typealias LinkdingApiKey = String

@LinkdingDsl
data class LinkdingApiConfig(
    val hostUrl: LinkdingHostUrl,
    val apiKey: LinkdingApiKey,
)

@LinkdingDsl
class LinkdingClientConfig {

    internal var httpClientBuilder: (() -> HttpClient)? = null
    internal var httpClientLoggingBlock: (Logging.Config.() -> Unit)? = null

    fun <T : HttpClientEngineConfig> httpClient(
        engineFactory: HttpClientEngineFactory<T>,
    ) {
        httpClientBuilder = {
            HttpClient(engineFactory)
        }
    }

    fun logging(block: Logging.Config.() -> Unit) {
        httpClientLoggingBlock = block
    }
}
