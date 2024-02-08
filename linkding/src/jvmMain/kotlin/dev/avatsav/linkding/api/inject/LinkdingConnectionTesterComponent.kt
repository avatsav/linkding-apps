package dev.avatsav.linkding.api.inject

import dev.avatsav.linkding.AppInfo
import dev.avatsav.linkding.Logger
import dev.avatsav.linkding.api.LinkdingConnectionTester
import dev.avatsav.linkding.inject.AppScope
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.logging.LogLevel
import me.tatarka.inject.annotations.Provides

actual interface LinkdingConnectionTesterComponent {

    @AppScope
    @Provides
    fun provideLinkdingConnectionTester(
        appInfo: AppInfo,
        appLogger: Logger,
    ): LinkdingConnectionTester {
        return LinkdingConnectionTester {
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
