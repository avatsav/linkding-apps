package dev.avatsav.linkding.inject

import io.github.aakira.napier.LogLevel as NapierLogLevel
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.KotlinxSerializationConverter
import kotlinx.serialization.json.Json

internal fun httpClient(enableNetworkLogs: Boolean) = HttpClient {
    install(ContentNegotiation) {
        val jsonConverter = KotlinxSerializationConverter(Json {
            explicitNulls = false
            isLenient = true
            ignoreUnknownKeys = true
            coerceInputValues = true
            encodeDefaults = true
            prettyPrint = enableNetworkLogs
        })
        register(ContentType.Application.Json, jsonConverter)
        register(ContentType.Application.Any, jsonConverter)
    }

    if (enableNetworkLogs) {
        install(Logging) {
            logger = KtorNapierLogger()
            level = LogLevel.BODY
        }
    }
}

internal class KtorNapierLogger : Logger {
    override fun log(message: String) {
        Napier.log(
            priority = NapierLogLevel.DEBUG, tag = "Ktor", message = message
        )
    }
}
