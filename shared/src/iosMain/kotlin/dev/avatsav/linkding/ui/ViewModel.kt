package dev.avatsav.linkding.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

actual abstract class ViewModel {

    actual val viewModelScope: CoroutineScope
        get() = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    actual open fun onCleared() {
    }

    fun clear() {
        viewModelScope.cancel()
        onCleared()
    }
}
