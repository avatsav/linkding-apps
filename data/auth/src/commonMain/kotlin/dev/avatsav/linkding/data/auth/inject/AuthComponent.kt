package dev.avatsav.linkding.data.auth.inject

import dev.avatsav.linkding.AppInfo
import dev.avatsav.linkding.Logger
import dev.avatsav.linkding.api.LinkdingAuthentication
import dev.avatsav.linkding.inject.AppScope
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.logging.LogLevel
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(AppScope::class)
interface AuthComponent {

    @Provides
    @SingleIn(AppScope::class)
    fun provideLinkdingAuthentication(
        httpClientEngineFactory: HttpClientEngineFactory<*>,
        appInfo: AppInfo,
        appLogger: Logger,
    ): LinkdingAuthentication = LinkdingAuthentication {
        httpClient(httpClientEngineFactory)
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
