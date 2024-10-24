package dev.avatsav.linkding.data.auth.inject

import co.touchlab.kermit.Logger
import com.r0adkll.kimchi.annotations.ContributesTo
import dev.avatsav.linkding.AppInfo
import dev.avatsav.linkding.api.LinkdingAuthentication
import dev.avatsav.linkding.inject.AppScope
import dev.avatsav.linkding.inject.annotations.SingleIn
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.logging.LogLevel
import me.tatarka.inject.annotations.Provides

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
            logger = object : io.ktor.client.plugins.logging.Logger {
                override fun log(message: String) {
                    Logger.d { message }
                }
            }
            level = when {
                appInfo.debug -> LogLevel.HEADERS
                else -> LogLevel.NONE
            }
        }
    }
}
