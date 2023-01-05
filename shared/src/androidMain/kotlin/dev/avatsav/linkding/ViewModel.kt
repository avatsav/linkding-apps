package dev.avatsav.linkding

import androidx.lifecycle.viewModelScope as androidViewModelScope
import kotlinx.coroutines.CoroutineScope

actual abstract class ViewModel : androidx.lifecycle.ViewModel() {

    actual val viewModelScope: CoroutineScope
        get() = androidViewModelScope

    actual override fun onCleared() {}

}