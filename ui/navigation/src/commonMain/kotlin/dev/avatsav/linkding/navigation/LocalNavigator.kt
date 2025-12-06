package dev.avatsav.linkding.navigation

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf

val LocalNavigator: ProvidableCompositionLocal<Navigator> = compositionLocalOf {
  error("No Navigator provided")
}

val LocalNavigationResultHandler: ProvidableCompositionLocal<NavigationResultHandler> =
  compositionLocalOf {
    error("No AnsweringResultHandler provided")
  }
