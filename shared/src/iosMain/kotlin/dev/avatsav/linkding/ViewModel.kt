package dev.avatsav.linkding

import com.rickclephas.kmp.nativecoroutines.NativeCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

actual abstract class ViewModel {

    @NativeCoroutineScope
    actual val viewModelScope: CoroutineScope
        get() = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    actual open fun onCleared() {
    }

    fun clear() {
        viewModelScope.cancel()
        onCleared()
    }

}