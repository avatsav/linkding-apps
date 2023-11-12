package dev.avatsav.linkding.api.core

import io.ktor.client.plugins.logging.Logger as KtorLogger
import dev.avatsav.linkding.AppInfo
import dev.avatsav.linkding.Logger
import dev.avatsav.linkding.prefs.ApiConfiguration
import io.ktor.client.HttpClient
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.Url
import io.ktor.http.contentType
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.KotlinxSerializationConverter
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

internal object HttpClientFactory {

    @OptIn(ExperimentalSerializationApi::class)
    fun buildHttpClient(
        appInfo: AppInfo,
        appLogger: Logger,
        apiConfig: ApiConfiguration.Linkding,
    ): HttpClient {
        val httpClient = HttpClient {
            defaultRequest {
                header(HttpHeaders.Authorization, "Token ${apiConfig.apiKey}")
                url {
                    takeFrom(Url(apiConfig.host))
                }
                contentType(ContentType.Application.Json)
            }
            install(ContentNegotiation) {
                val jsonConverter = KotlinxSerializationConverter(
                    Json {
                        explicitNulls = false
                        isLenient = true
                        ignoreUnknownKeys = true
                        coerceInputValues = true
                        encodeDefaults = true
                        prettyPrint = true
                    },
                )
                register(ContentType.Application.Json, jsonConverter)
                register(ContentType.Application.Any, jsonConverter)
            }
            install(HttpCache)
            install(Logging) {
                logger = LinkdingLogger(appLogger)
                level = when {
                    appInfo.debug -> LogLevel.HEADERS
                    else -> LogLevel.NONE
                }
            }
        }
        return httpClient
    }
}

internal class LinkdingLogger(private val logger: Logger) : KtorLogger {
    override fun log(message: String) {
        logger.d { message }
    }
}

