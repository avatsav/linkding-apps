package dev.avatsav.linkding.viewmodel

import app.cash.molecule.AndroidUiDispatcher
import app.cash.molecule.RecompositionMode
import kotlin.coroutines.CoroutineContext

internal actual val PlatformUiCoroutineContext: CoroutineContext = AndroidUiDispatcher.Main

internal actual val PlatformRecompositionMode: RecompositionMode = RecompositionMode.ContextClock
