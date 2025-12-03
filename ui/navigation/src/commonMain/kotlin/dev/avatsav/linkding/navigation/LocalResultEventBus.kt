package dev.avatsav.linkding.navigation

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf

val LocalResultEventBus: ProvidableCompositionLocal<ResultEventBus> = compositionLocalOf {
  error("No ResultEventBus provided")
}
