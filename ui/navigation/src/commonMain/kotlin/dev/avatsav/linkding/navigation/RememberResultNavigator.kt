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
 * Creates a type-safe navigator that receives results from a specific screen type.
 *
 * This composable returns a [ScreenNavigator] constrained to only navigate to screens of type [S],
 * ensuring compile-time safety that you can't navigate to a different screen and expect results.
 * When the target screen pops with a result, the [onResult] callback is invoked.
 *
 * ## Usage
 *
 * ```kotlin
 * @Composable
 * fun BookmarksScreen(viewModel: BookmarksViewModel) {
 *   val tagsNavigator = rememberResultNavigator<Screen.Tags, Screen.Tags.Result> { result ->
 *     when (result) {
 *       is Screen.Tags.Result.Confirmed -> viewModel.setTags(result.selectedTags)
 *       Screen.Tags.Result.Dismissed -> { /* handle dismissal */ }
 *     }
 *   }
 *
 *   // Navigate using invoke operator
 *   Button(onClick = { tagsNavigator(Screen.Tags(selectedTagIds)) }) {
 *     Text("Select Tags")
 *   }
 *
 *   // Or using goTo method
 *   Button(onClick = { tagsNavigator.goTo(Screen.Tags(selectedTagIds)) }) {
 *     Text("Select Tags")
 *   }
 *
 *   // This would NOT compile - type safety!
 *   // tagsNavigator(Screen.Settings) // Error: Type mismatch
 * }
 * ```
 *
 * ## How It Works
 * 1. When you navigate via the returned [ScreenNavigator], it registers with
 *    [NavigationResultHandler]
 * 2. The target screen can pop with a result via `navigator.pop(result)`
 * 3. When you return to this screen, the result is delivered to [onResult]
 *
 * @param S The screen type to navigate to (must implement [ScreenWithResult])
 * @param R The result type expected from the target screen (must implement [NavResult])
 * @param onResult Callback invoked when the target screen returns a result
 * @return A [ScreenNavigator] constrained to only navigate to screens of type [S]
 * @see ScreenNavigator
 * @see ScreenWithResult
 * @see NavResult
 * @see Navigator.pop
 */
@Composable
inline fun <reified S, reified R : NavResult> rememberResultNavigator(
  noinline onResult: (R) -> Unit
): ScreenNavigator<S> where S : Screen, S : ScreenWithResult<R> {
  val navigator = LocalNavigator.current
  val resultHandler = LocalNavigationResultHandler.current

  val currentOnResult by rememberUpdatedState(onResult)

  // Capture the current screen's key - this is the caller
  val callerKey = rememberSaveable { navigator.currentScreen?.key ?: error("No current screen") }

  // Use the standard result key that NavigatorImpl uses when popping
  val resultKey = NavigationResultHandler.RESULT_KEY_FROM_POP

  val hasNavigated = remember { mutableStateOf(false) }

  // When we return to our screen, check for results
  val currentScreenKey = navigator.currentScreen?.key
  if (hasNavigated.value && currentScreenKey == callerKey) {
    LaunchedEffect(callerKey) {
      when (val result = resultHandler.awaitResult(callerKey, resultKey)) {
        null -> {
          // No result available
        }
        is R -> {
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

  // Clean up when the composable leaves composition (e.g., screen removed from backstack)
  DisposableEffect(callerKey, resultKey) {
    onDispose {
      // Cancel any pending result awaits to prevent memory leaks
      resultHandler.cancelAwait(callerKey, resultKey)
    }
  }

  return remember(navigator, resultHandler, callerKey, resultKey) {
    object : ScreenNavigator<S> {
      override fun goTo(screen: S): Boolean {
        resultHandler.prepareForResult(callerKey, resultKey)
        hasNavigated.value = true
        return navigator.goTo(screen)
      }
    }
  }
}
