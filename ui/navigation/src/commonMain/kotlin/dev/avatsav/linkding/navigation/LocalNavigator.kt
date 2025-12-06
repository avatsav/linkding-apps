package dev.avatsav.linkding.navigation

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf

/**
 * CompositionLocal providing access to the current [Navigator].
 *
 * This is provided automatically by [NavigatorCompositionLocals]:
 * ```kotlin
 * val backStack = rememberScreenBackStack(savedStateConfig, startScreen)
 * val navigator = rememberNavigator(backStack, onOpenUrl)
 *
 * NavigatorCompositionLocals(navigator) {
 *   // App content - LocalNavigator is available here
 * }
 * ```
 *
 * Access the navigator in any composable:
 * ```kotlin
 * val navigator = LocalNavigator.current
 * navigator.goTo(Screen.Settings)
 * ```
 *
 * @see Navigator
 * @see NavigatorCompositionLocals
 * @see rememberNavigator
 * @see rememberScreenBackStack
 */
val LocalNavigator: ProvidableCompositionLocal<Navigator> = compositionLocalOf {
  error("No Navigator provided. Ensure LocalNavigator is provided in your composition hierarchy.")
}

/**
 * CompositionLocal providing access to the [NavigationResultHandler].
 *
 * This is an internal implementation detail. Users should use [NavigatorCompositionLocals] which
 * provides this automatically.
 *
 * @see NavigatorCompositionLocals
 * @see rememberResultNavigator
 */
@PublishedApi
internal val LocalNavigationResultHandler: ProvidableCompositionLocal<NavigationResultHandler> =
  compositionLocalOf {
    error("No NavigationResultHandler provided. Ensure you're using NavigatorCompositionLocals.")
  }
