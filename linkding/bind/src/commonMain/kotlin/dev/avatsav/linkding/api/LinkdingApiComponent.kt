package dev.avatsav.linkding.api

import dev.avatsav.linkding.AppInfo
import dev.avatsav.linkding.Logger
import dev.avatsav.linkding.inject.AppScope
import io.ktor.client.plugins.logging.LogLevel
import me.tatarka.inject.annotations.Provides

interface LinkdingApiComponent {

    @Provides
    @AppScope
    fun provideLinkdingConnectionApi(
        appInfo: AppInfo,
        appLogger: Logger,
    ): LinkdingConnectionApi {
        return LinkdingConnectionApi {
            httpClient(httpClientEngine)
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
