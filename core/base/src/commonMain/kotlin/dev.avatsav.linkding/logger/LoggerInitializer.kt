package dev.avatsav.linkding.logger

import co.touchlab.kermit.Severity
import dev.avatsav.linkding.AppInfo
import dev.avatsav.linkding.Initializer
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

@Inject
@ContributesBinding(AppScope::class, multibinding = true)
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
