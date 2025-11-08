package dev.avatsav.linkding.viewmodel

import app.cash.molecule.AndroidUiDispatcher
import kotlin.coroutines.CoroutineContext

actual val UiDispatcherContext: CoroutineContext = AndroidUiDispatcher.Main
