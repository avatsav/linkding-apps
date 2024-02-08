package dev.avatsav.linkding

import dev.avatsav.linkding.api.Linkding
import dev.avatsav.linkding.api.LinkdingApiConfig
import dev.avatsav.linkding.data.model.ApiConfig
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.logging.LogLevel
import me.tatarka.inject.annotations.Provides

actual interface LinkdingApiPlatformComponent {

    @Provides
    fun provideLinkding(
        appInfo: AppInfo,
        appLogger: Logger,
        apiConfig: ApiConfig,
    ): Linkding {
        val linkdingApiConfig = LinkdingApiConfig(apiConfig.hostUrl, apiConfig.apiKey)
        return Linkding(linkdingApiConfig) {
            httpClient(OkHttp)
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
