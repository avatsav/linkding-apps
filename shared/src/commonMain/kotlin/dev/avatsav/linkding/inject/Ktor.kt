package dev.avatsav.linkding.inject

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

internal fun httpClient(enableNetworkLogs: Boolean) =
    HttpClient {
        install(ContentNegotiation) {
            json(Json {
                explicitNulls = false
                isLenient = true
                ignoreUnknownKeys = true
                coerceInputValues = true
                encodeDefaults = true
                prettyPrint = enableNetworkLogs
                classDiscriminator = "#class"
            })
        }

        if (enableNetworkLogs) {
            install(Logging) {
                logger = NapierLogger()
                level = LogLevel.BODY
            }
        }
    }

internal class NapierLogger : Logger {
    override fun log(message: String) {
        Napier.log(
            priority = io.github.aakira.napier.LogLevel.DEBUG,
            tag = "Ktor",
            message = message
        )
    }
}
