package dev.avatsav.linkding.auth.inject

import dev.avatsav.linkding.AppInfo
import dev.avatsav.linkding.api.LinkdingAuthentication
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger

@ContributesTo(AppScope::class)
interface AuthComponent {

  @Provides
  @SingleIn(AppScope::class)
  fun provideLinkdingAuthentication(appInfo: AppInfo): LinkdingAuthentication =
    LinkdingAuthentication {
      logging {
        logger =
          object : Logger {
            override fun log(message: String) {
              co.touchlab.kermit.Logger.d { message }
            }
          }
        level =
          when {
            appInfo.debug -> LogLevel.HEADERS
            else -> LogLevel.NONE
          }
      }
    }
}
