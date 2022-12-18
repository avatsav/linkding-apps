package dev.avatsav.linkding.inject

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

internal fun httpClient(enableNetworkLogs: Boolean) = HttpClient {
    install(ContentNegotiation) {
        json(Json {
            isLenient = true
            ignoreUnknownKeys = true
            encodeDefaults = true
            prettyPrint = enableNetworkLogs
            classDiscriminator = "#class"
        })
    }
    // TODO: Add response validator that checks 4xx statuses, network errors etc
    // TODO: Add interceptor -> set Authorization header
    // REF: scoped httpClients (logged in version)
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
