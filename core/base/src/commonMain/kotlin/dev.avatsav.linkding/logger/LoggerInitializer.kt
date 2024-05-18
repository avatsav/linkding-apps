package dev.avatsav.linkding.logger

import co.touchlab.kermit.Severity
import com.r0adkll.kimchi.annotations.ContributesMultibinding
import dev.avatsav.linkding.AppInfo
import dev.avatsav.linkding.Initializer
import dev.avatsav.linkding.inject.AppScope
import me.tatarka.inject.annotations.Inject

@ContributesMultibinding(AppScope::class)
@Inject
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
