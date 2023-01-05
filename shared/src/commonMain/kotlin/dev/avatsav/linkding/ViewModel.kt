package dev.avatsav.linkding

import com.rickclephas.kmp.nativecoroutines.NativeCoroutineScope
import kotlinx.coroutines.CoroutineScope

expect abstract class ViewModel() {

    @NativeCoroutineScope
    val viewModelScope: CoroutineScope

    protected fun onCleared()

}