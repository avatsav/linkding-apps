package dev.avatsav.linkding.viewmodel

import app.cash.molecule.AndroidUiDispatcher
import app.cash.molecule.RecompositionMode
import kotlin.coroutines.CoroutineContext

internal actual val PlatformMainDispatcher: CoroutineContext = AndroidUiDispatcher.Main

internal actual val PlatformRecompositionMode: RecompositionMode = RecompositionMode.ContextClock
