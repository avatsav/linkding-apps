package dev.avatsav.linkding

import com.rickclephas.kmp.nativecoroutines.NativeCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

abstract class Presenter {
    var cleared = false
        private set

    @NativeCoroutineScope
    val presenterScope: CoroutineScope by lazy {
        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    }

    open fun onCleared() {}

    fun clear() {
        cleared = true
        clearCoroutineScopeWithRuntimeException()
        onCleared()
    }

    private fun clearCoroutineScopeWithRuntimeException() {
        try {
            presenterScope.cancel()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}

