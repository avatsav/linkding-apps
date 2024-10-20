package dev.avatsav.linkding

import co.touchlab.kermit.Severity
import dev.avatsav.linkding.inject.AppScope
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import co.touchlab.kermit.Logger as Kermit

interface Logger {
    fun v(throwable: Throwable? = null, message: () -> String = { "" }) = Unit
    fun d(throwable: Throwable? = null, message: () -> String = { "" }) = Unit
    fun i(throwable: Throwable? = null, message: () -> String = { "" }) = Unit
    fun e(throwable: Throwable? = null, message: () -> String = { "" }) = Unit
    fun w(throwable: Throwable? = null, message: () -> String = { "" }) = Unit
}

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class KermitLogger(appInfo: AppInfo) : Logger {
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
