package dev.avatsav.linkding.logger

import co.touchlab.kermit.Severity
import dev.avatsav.linkding.AppInfo
import dev.avatsav.linkding.Initializer
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject

@Inject
@ContributesIntoSet(AppScope::class)
class LoggerInitializer(private val appInfo: AppInfo) : Initializer {
  override fun initialize() {
    co.touchlab.kermit.Logger.setMinSeverity(
      when {
        appInfo.debug -> Severity.Debug
        else -> Severity.Error
      },
    )
  }
}
