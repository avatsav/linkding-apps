package dev.avatsav.linkding.data.bookmarks.inject

import com.r0adkll.kimchi.annotations.ContributesTo
import dev.avatsav.linkding.AppInfo
import dev.avatsav.linkding.Logger
import dev.avatsav.linkding.api.Linkding
import dev.avatsav.linkding.api.LinkdingApiConfig
import dev.avatsav.linkding.api.LinkdingBookmarksApi
import dev.avatsav.linkding.api.LinkdingTagsApi
import dev.avatsav.linkding.data.model.ApiConfig
import dev.avatsav.linkding.inject.UserScope
import dev.avatsav.linkding.inject.annotations.SingleIn
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.logging.LogLevel
import me.tatarka.inject.annotations.Provides

@ContributesTo(UserScope::class)
interface LinkdingComponent {

    @Provides
    @SingleIn(UserScope::class)
    fun provideLinkding(
        apiConfig: ApiConfig,
        httpClientEngineFactory: HttpClientEngineFactory<*>,
        appInfo: AppInfo,
        appLogger: Logger,
    ): Linkding = Linkding(
        LinkdingApiConfig(
            apiConfig.hostUrl,
            apiConfig.apiKey,
        ),
    ) {
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

    @Provides
    @SingleIn(UserScope::class)
    fun provideBookmarksApi(linkding: Linkding): LinkdingBookmarksApi = linkding.bookmarks

    @Provides
    @SingleIn(UserScope::class)
    fun provideTagsApi(linkding: Linkding): LinkdingTagsApi = linkding.tags
}
