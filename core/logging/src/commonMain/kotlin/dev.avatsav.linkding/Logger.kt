package dev.avatsav.linkding

import com.r0adkll.kimchi.annotations.ContributesBinding
import dev.avatsav.linkding.inject.AppScope
import dev.avatsav.linkding.inject.annotations.SingleIn
import me.tatarka.inject.annotations.Inject

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
class FakeLogger(appInfo: AppInfo) : Logger
