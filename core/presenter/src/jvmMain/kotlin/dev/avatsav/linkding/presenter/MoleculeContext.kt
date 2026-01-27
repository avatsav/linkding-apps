package dev.avatsav.linkding.presenter

import app.cash.molecule.RecompositionMode
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers

internal actual val PlatformMainDispatcher: CoroutineContext = Dispatchers.Main.immediate

internal actual val PlatformRecompositionMode: RecompositionMode = RecompositionMode.Immediate
