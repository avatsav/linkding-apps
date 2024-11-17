package dev.avatsav.linkding.auth.inject

import dev.avatsav.linkding.AppInfo
import dev.avatsav.linkding.api.LinkdingAuthentication
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(AppScope::class)
interface AuthComponent {

    @Provides
    @SingleIn(AppScope::class)
    fun provideLinkdingAuthentication(
        httpClientEngineFactory: HttpClientEngineFactory<*>,
        appInfo: AppInfo,
    ): LinkdingAuthentication = LinkdingAuthentication {
        httpClient(httpClientEngineFactory)
        logging {
            logger = object : Logger {
                override fun log(message: String) {
                    co.touchlab.kermit.Logger.d { message }
                }
            }
            level = when {
                appInfo.debug -> LogLevel.HEADERS
                else -> LogLevel.NONE
            }
        }
    }
}
