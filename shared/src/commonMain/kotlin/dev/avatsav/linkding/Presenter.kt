package dev.avatsav.linkding

import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

abstract class Presenter {

    val presenterScope: CoroutineScope by lazy {
        CloseableCoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    }

    open fun clear() {
        presenterScope.cancel()
    }

}


internal class CloseableCoroutineScope(context: CoroutineContext) : Closeable, CoroutineScope {

    override val coroutineContext: CoroutineContext = context

    override fun close() {
        coroutineContext.cancel()
    }
}

internal interface Closeable {
    fun close()
}