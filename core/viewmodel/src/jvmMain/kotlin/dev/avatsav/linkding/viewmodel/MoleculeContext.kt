package dev.avatsav.linkding.viewmodel

import app.cash.molecule.RecompositionMode
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

internal actual val PlatformUiCoroutineContext: CoroutineContext = Dispatchers.Main

internal actual val PlatformRecompositionMode: RecompositionMode = RecompositionMode.Immediate
