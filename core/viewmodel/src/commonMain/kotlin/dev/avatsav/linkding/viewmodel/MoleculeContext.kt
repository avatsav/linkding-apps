package dev.avatsav.linkding.viewmodel

import app.cash.molecule.RecompositionMode
import kotlin.coroutines.CoroutineContext

internal expect val PlatformUiCoroutineContext: CoroutineContext

internal expect val PlatformRecompositionMode: RecompositionMode
