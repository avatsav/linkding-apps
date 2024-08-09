package dev.avatsav.linkding.api

import dev.avatsav.linkding.AppCoroutineScope
import dev.avatsav.linkding.AppInfo
import dev.avatsav.linkding.Logger
import dev.avatsav.linkding.data.model.ApiConfig
import dev.avatsav.linkding.prefs.AppPreferences
import io.ktor.client.plugins.logging.LogLevel
import me.tatarka.inject.annotations.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@Inject
class LinkdingApiProvider(
    private val appInfo: AppInfo,
    private val appLogger: Logger,
    appPreferences: AppPreferences,
    coroutineScope: AppCoroutineScope,
) {
    private val apiConfig: StateFlow<ApiConfig?> =
        appPreferences.observeApiConfig()
            .stateIn(coroutineScope, SharingStarted.Eagerly, appPreferences.getApiConfig())

    private val linkding: Linkding by lazy {
        val linkdingApiConfig = LinkdingApiConfig(
            apiConfig.value!!.hostUrl,
            apiConfig.value!!.apiKey,
        )
        Linkding(linkdingApiConfig) {
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

    val bookmarksApi: LinkdingBookmarksApi by lazy { linkding.bookmarks }
    val tagsApi: LinkdingTagsApi by lazy { linkding.tags }
}
