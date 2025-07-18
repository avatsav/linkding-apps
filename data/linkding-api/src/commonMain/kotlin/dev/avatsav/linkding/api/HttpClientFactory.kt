package dev.avatsav.linkding.api

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineFactory
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
import kotlinx.serialization.json.Json

internal expect fun httpClientEngineFactory(): HttpClientEngineFactory<*>

internal object HttpClientFactory {

  fun buildHttpClient(
    clientConfig: LinkdingClientConfig,
    apiConfig: LinkdingApiConfig? = null,
  ): HttpClient {
    val defaultHttpConfig: HttpClientConfig<*>.() -> Unit = {
      defaultRequest {
        if (apiConfig != null) {
          header(HttpHeaders.Authorization, "Token ${apiConfig.apiKey}")
          url { takeFrom(Url(apiConfig.hostUrl)) }
        }
        contentType(ContentType.Application.Json)
      }

      expectSuccess = true

      install(ContentNegotiation) {
        json(
          Json {
            isLenient = true
            ignoreUnknownKeys = true
            coerceInputValues = true
            encodeDefaults = true
            prettyPrint = false
            explicitNulls = false
          }
        )
      }

      install(HttpCache)

      clientConfig.httpClientLoggingBlock?.let { Logging(it) }
    }
    return clientConfig.httpClientBuilder?.invoke()?.config(defaultHttpConfig)
      ?: HttpClient(defaultHttpConfig)
  }
}
