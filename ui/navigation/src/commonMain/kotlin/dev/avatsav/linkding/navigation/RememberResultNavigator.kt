package dev.avatsav.linkding.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable

/**
 * Creates a type-safe [RouteNavigator] for navigating to result-returning routes.
 *
 * ```kotlin
 * val tagsNavigator = rememberResultNavigator<Route.Tags, Route.Tags.Result> { result ->
 *   when (result) {
 *     is Route.Tags.Result.Confirmed -> handleTags(result.selectedTags)
 *     Route.Tags.Result.Dismissed -> { }
 *   }
 * }
 * tagsNavigator(Route.Tags(selectedTagIds))
 * ```
 */
@Composable
inline fun <reified R, reified T : NavResult> rememberResultNavigator(
  noinline onResult: (T) -> Unit
): RouteNavigator<R> where R : Route, R : RouteWithResult<T> {
  val navigator = LocalNavigator.current
  val resultHandler = LocalNavigationResultHandler.current

  val currentOnResult by rememberUpdatedState(onResult)

  // Capture the current route's key - this is the caller
  val callerKey = rememberSaveable { navigator.currentRoute?.key ?: error("No current route") }

  // Use the standard result key that NavigatorImpl uses when popping
  val resultKey = NavigationResultHandler.RESULT_KEY_FROM_POP

  val hasNavigated = remember { mutableStateOf(false) }

  // When we return to our route, check for results
  val currentRouteKey = navigator.currentRoute?.key
  if (hasNavigated.value && currentRouteKey == callerKey) {
    LaunchedEffect(callerKey) {
      when (val result = resultHandler.awaitResult(callerKey, resultKey)) {
        null -> {
          // No result available
        }
        is T -> {
          hasNavigated.value = false
          currentOnResult(result)
        }

        else -> {
          // Type mismatch - silently ignore
          hasNavigated.value = false
        }
      }
    }
  }

  // Clean up when the composable leaves composition (e.g., route removed from backstack)
  DisposableEffect(callerKey, resultKey) {
    onDispose { resultHandler.cancelAwait(callerKey, resultKey) }
  }

  return remember(navigator, resultHandler, callerKey, resultKey) {
    object : RouteNavigator<R> {
      override fun goTo(route: R): Boolean {
        resultHandler.prepareForResult(callerKey, resultKey)
        hasNavigated.value = true
        return navigator.goTo(route)
      }
    }
  }
}
