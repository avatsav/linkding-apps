package dev.avatsav.linkding

import co.touchlab.kermit.Severity
import me.tatarka.inject.annotations.Inject

interface Logger {
    fun v(throwable: Throwable? = null, message: () -> String = { "" }) = Unit
    fun d(throwable: Throwable? = null, message: () -> String = { "" }) = Unit
    fun i(throwable: Throwable? = null, message: () -> String = { "" }) = Unit
    fun e(throwable: Throwable? = null, message: () -> String = { "" }) = Unit
    fun w(throwable: Throwable? = null, message: () -> String = { "" }) = Unit
}

@Inject
internal class KermitLogger(appInfo: AppInfo) : Logger {
    init {
        co.touchlab.kermit.Logger.setMinSeverity(
            when {
                appInfo.debug -> Severity.Debug
                else -> Severity.Error
            },
        )
    }

    override fun v(throwable: Throwable?, message: () -> String) {
        co.touchlab.kermit.Logger.v(throwable = throwable, message = message)
    }

    override fun d(throwable: Throwable?, message: () -> String) {
        co.touchlab.kermit.Logger.d(throwable = throwable, message = message)
    }

    override fun i(throwable: Throwable?, message: () -> String) {
        co.touchlab.kermit.Logger.i(throwable = throwable, message = message)
    }

    override fun e(throwable: Throwable?, message: () -> String) {
        co.touchlab.kermit.Logger.e(throwable = throwable, message = message)
    }

    override fun w(throwable: Throwable?, message: () -> String) {
        co.touchlab.kermit.Logger.w(throwable = throwable, message = message)
    }
}
