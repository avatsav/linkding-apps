package dev.avatsav.linkding.viewmodel

import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers

actual val UiDispatcherContext: CoroutineContext = Dispatchers.Main
