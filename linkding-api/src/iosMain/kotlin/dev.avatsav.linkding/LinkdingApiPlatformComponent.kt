package dev.avatsav.linkding

import dev.avatsav.linkding.api.Linkding
import dev.avatsav.linkding.api.LinkdingApiConfig
import dev.avatsav.linkding.data.model.ApiConfig
import dev.avatsav.linkding.inject.AppScope
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.logging.LogLevel
import me.tatarka.inject.annotations.Provides

actual interface LinkdingApiPlatformComponent {

    @AppScope
    @Provides
    fun provideLinkding(
        appInfo: AppInfo,
        appLogger: Logger,
        apiConfig: ApiConfig.Linkding,
    ): Linkding {
        val linkdingApiConfig = LinkdingApiConfig(apiConfig.hostUrl, apiConfig.apiKey)
        return Linkding(linkdingApiConfig) {
            httpClient(Darwin)
            logging {
                logger = object : io.ktor.client.plugins.logging.Logger {
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
