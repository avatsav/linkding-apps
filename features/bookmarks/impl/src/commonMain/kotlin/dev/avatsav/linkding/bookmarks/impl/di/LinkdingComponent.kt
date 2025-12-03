package dev.avatsav.linkding.bookmarks.impl.di

import co.touchlab.kermit.Logger
import dev.avatsav.linkding.AppInfo
import dev.avatsav.linkding.api.Linkding
import dev.avatsav.linkding.api.LinkdingApiConfig
import dev.avatsav.linkding.api.LinkdingBookmarksApi
import dev.avatsav.linkding.api.LinkdingTagsApi
import dev.avatsav.linkding.data.model.ApiConfig
import dev.avatsav.linkding.di.scope.UserScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import io.ktor.client.plugins.logging.LogLevel

@ContributesTo(UserScope::class)
interface LinkdingComponent {

  @Provides
  @SingleIn(UserScope::class)
  fun provideLinkding(apiConfig: ApiConfig, appInfo: AppInfo): Linkding =
    Linkding(LinkdingApiConfig(apiConfig.hostUrl, apiConfig.apiKey)) {
      logging {
        logger =
          object : io.ktor.client.plugins.logging.Logger {
            override fun log(message: String) {
              Logger.d { message }
            }
          }
        level =
          when {
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
