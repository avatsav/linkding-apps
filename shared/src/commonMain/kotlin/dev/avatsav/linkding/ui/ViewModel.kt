package dev.avatsav.linkding.ui

import kotlinx.coroutines.CoroutineScope

expect abstract class ViewModel() {

    val viewModelScope: CoroutineScope

    protected fun onCleared()
}
