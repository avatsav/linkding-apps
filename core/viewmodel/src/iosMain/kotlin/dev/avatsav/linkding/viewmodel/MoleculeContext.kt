package dev.avatsav.linkding.viewmodel

import app.cash.molecule.DisplayLinkClock
import app.cash.molecule.RecompositionMode
import kotlin.coroutines.CoroutineContext

actual val PlatformUiCoroutineContext: CoroutineContext = DisplayLinkClock

actual val PlatformRecompositionMode: RecompositionMode = RecompositionMode.ContextClock
