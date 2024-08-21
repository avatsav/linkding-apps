package dev.avatsav.linkding.data.auth.inject

import dev.avatsav.linkding.AppInfo
import dev.avatsav.linkding.Logger
import dev.avatsav.linkding.api.LinkdingAuthentication
import dev.avatsav.linkding.data.auth.AuthManager
import dev.avatsav.linkding.data.auth.AuthRepository
import dev.avatsav.linkding.data.auth.DefaultAuthManager
import dev.avatsav.linkding.data.auth.internal.LinkdingAuthRepository
import dev.avatsav.linkding.inject.AppScope
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.logging.LogLevel
import me.tatarka.inject.annotations.Provides

interface AuthComponent {

    @Provides
    @AppScope
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

    @AppScope
    val LinkdingAuthRepository.bind: AuthRepository
        @Provides get() = this

    @AppScope
    val DefaultAuthManager.bind: AuthManager
        @Provides get() = this
}
