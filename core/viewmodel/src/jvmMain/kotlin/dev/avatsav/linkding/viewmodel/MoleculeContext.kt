package dev.avatsav.linkding.viewmodel

import app.cash.molecule.RecompositionMode
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers

internal actual val PlatformUiCoroutineContext: CoroutineContext = Dispatchers.Main

internal actual val PlatformRecompositionMode: RecompositionMode = RecompositionMode.Immediate
