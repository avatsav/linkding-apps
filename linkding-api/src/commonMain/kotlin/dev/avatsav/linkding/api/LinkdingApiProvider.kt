package dev.avatsav.linkding.api

import dev.avatsav.linkding.AppCoroutineDispatchers
import dev.avatsav.linkding.AppCoroutineScope
import dev.avatsav.linkding.AppInfo
import dev.avatsav.linkding.Logger
import dev.avatsav.linkding.data.config.ApiConfigRepository
import io.ktor.client.plugins.logging.LogLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class LinkdingApiProvider(
    private val appInfo: AppInfo,
    private val appLogger: Logger,
    private val configRepository: ApiConfigRepository,
    dispatchers: AppCoroutineDispatchers,
    coroutineScope: AppCoroutineScope,
) {
    private val apiConfig = MutableStateFlow(configRepository.apiConfig)

    init {
        coroutineScope.launch(dispatchers.io) {
            configRepository.observeApiConfiguration().collect {
                apiConfig.value = it
            }
        }
    }

    private val linkding: Linkding by lazy {
        val linkdingApiConfig =
            LinkdingApiConfig(apiConfig.value!!.hostUrl, apiConfig.value!!.apiKey)
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
