package dev.avatsav.linkding

interface Logger {
    fun v(throwable: Throwable? = null, message: () -> String = { "" }) = Unit
    fun d(throwable: Throwable? = null, message: () -> String = { "" }) = Unit
    fun i(throwable: Throwable? = null, message: () -> String = { "" }) = Unit
    fun e(throwable: Throwable? = null, message: () -> String = { "" }) = Unit
    fun w(throwable: Throwable? = null, message: () -> String = { "" }) = Unit
}
