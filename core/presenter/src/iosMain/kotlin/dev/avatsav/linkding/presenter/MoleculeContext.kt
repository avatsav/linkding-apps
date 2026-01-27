package dev.avatsav.linkding.presenter

import app.cash.molecule.DisplayLinkClock
import app.cash.molecule.RecompositionMode
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers

internal actual val PlatformMainDispatcher: CoroutineContext = DisplayLinkClock + Dispatchers.Main

actual val PlatformRecompositionMode: RecompositionMode = RecompositionMode.ContextClock
