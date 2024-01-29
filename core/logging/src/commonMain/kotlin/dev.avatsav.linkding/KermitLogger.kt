package dev.avatsav.linkding

import co.touchlab.kermit.Logger as Kermit
import co.touchlab.kermit.Severity
import me.tatarka.inject.annotations.Inject

@Inject
internal class KermitLogger(appInfo: AppInfo) : Logger {
    init {
        Kermit.setMinSeverity(
            when {
                appInfo.debug -> Severity.Debug
                else -> Severity.Error
            },
        )
    }

    override fun v(throwable: Throwable?, message: () -> String) {
        Kermit.v(throwable = throwable, message = message)
    }

    override fun d(throwable: Throwable?, message: () -> String) {
        Kermit.d(throwable = throwable, message = message)
    }

    override fun i(throwable: Throwable?, message: () -> String) {
        Kermit.i(throwable = throwable, message = message)
    }

    override fun e(throwable: Throwable?, message: () -> String) {
        Kermit.e(throwable = throwable, message = message)
    }

    override fun w(throwable: Throwable?, message: () -> String) {
        Kermit.w(throwable = throwable, message = message)
    }
}
