package dev.avatsav.linkding.api.inject

import dev.avatsav.linkding.AppInfo
import dev.avatsav.linkding.Logger
import dev.avatsav.linkding.api.Linkding
import dev.avatsav.linkding.api.LinkdingApiConfig
import dev.avatsav.linkding.prefs.ApiConfiguration
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger as KtorLogger
import me.tatarka.inject.annotations.Provides

actual interface LinkdingApiPlatformComponent {

    @Provides
    fun provideLinkding(
        appInfo: AppInfo,
        appLogger: Logger,
        apiConfig: ApiConfiguration.Linkding,
    ): Linkding {
        val linkdingApiConfig = LinkdingApiConfig(apiConfig.hostUrl, apiConfig.apiKey)
        return Linkding(linkdingApiConfig) {
            httpClient(Darwin)
            logging {
                logger = object : KtorLogger {
                    override fun log(message: String) {
                        appLogger.d { message }
                    }
                }
                level = when {
                    appInfo.debug -> LogLevel.HEADERS
                    else -> LogLevel.NONE
                }
            }
        }
    }
}