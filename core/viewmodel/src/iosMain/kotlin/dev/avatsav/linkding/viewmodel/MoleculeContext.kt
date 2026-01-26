package dev.avatsav.linkding.viewmodel

import app.cash.molecule.DisplayLinkClock
import app.cash.molecule.RecompositionMode
import kotlin.coroutines.CoroutineContext

internal actual val PlatformMainDispatcher: CoroutineContext = DisplayLinkClock

actual val PlatformRecompositionMode: RecompositionMode = RecompositionMode.ContextClock
