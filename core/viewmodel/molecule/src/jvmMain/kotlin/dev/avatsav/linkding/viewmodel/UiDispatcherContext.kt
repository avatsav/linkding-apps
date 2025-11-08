package dev.avatsav.linkding.viewmodel

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

actual val UiDispatcherContext: CoroutineContext = Dispatchers.Main
