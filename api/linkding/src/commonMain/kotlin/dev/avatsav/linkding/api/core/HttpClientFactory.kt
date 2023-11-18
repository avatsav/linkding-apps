package dev.avatsav.linkding.api.core

import dev.avatsav.linkding.api.LinkdingClientConfig
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.Url
import io.ktor.http.contentType
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

internal object HttpClientFactory {

    @OptIn(ExperimentalSerializationApi::class)
    fun buildHttpClient(config: LinkdingClientConfig): HttpClient {
        val defaultHttpConfig: HttpClientConfig<*>.() -> Unit = {
            defaultRequest {
                header(HttpHeaders.Authorization, "Token ${config.apiConfig.apiKey}")
                url {
                    takeFrom(Url(config.apiConfig.hostUrl))
                }
                contentType(ContentType.Application.Json)
            }

            install(ContentNegotiation) {
                json(
                    Json {
                        isLenient = true
                        ignoreUnknownKeys = true
                        coerceInputValues = true
                        encodeDefaults = true
                        prettyPrint = false
                        explicitNulls = false
                    },
                )
            }

            install(HttpCache)

            config.httpClientLoggingBlock?.let {
                Logging(it)
            }
        }
        return config.httpClientBuilder?.invoke()?.config(defaultHttpConfig) ?: HttpClient(
            defaultHttpConfig,
        )
    }
}

