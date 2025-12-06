package dev.avatsav.linkding.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateMap

/**
 * Coordinates result passing between screens during navigation.
 *
 * This handler manages the lifecycle of navigation results, ensuring that when a screen pops with a
 * result, that result is delivered to the correct caller screen. Results are stored temporarily and
 * consumed when the calling screen retrieves them.
 *
 * ## How It Works
 * 1. When a screen wants to receive a result, it registers via [prepareForResult]
 * 2. The target screen pops with a result via [Navigator.pop]
 * 3. [NavigatorImpl] routes the result via [sendResult]
 * 4. The original screen retrieves the result via [awaitResult]
 *
 * ## Usage
 *
 * This class is used internally by [rememberResultNavigator] and [Navigator]. You typically don't
 * interact with it directly. Instead, use:
 * ```kotlin
 * // In the calling screen
 * val tagsNavigator = rememberResultNavigator<Screen.Tags, Screen.Tags.Result> { result ->
 *   // Handle result
 * }
 * tagsNavigator(Screen.Tags())
 *
 * // In the target screen
 * navigator.pop(Screen.Tags.Result.Confirmed(selectedTags))
 * ```
 *
 * @see rememberResultNavigator
 * @see Navigator.pop
 * @see LocalNavigationResultHandler
 */
@Stable
class NavigationResultHandler
internal constructor(
  private val pendingResults: SnapshotStateMap<Pair<String, String>, NavResult> =
    mutableStateMapOf(),
  private val awaitingResults: SnapshotStateMap<Pair<String, String>, Boolean> = mutableStateMapOf(),
) {
  /**
   * Register that a screen is expecting a result.
   *
   * @param callerKey The key of the screen that will receive the result
   * @param resultKey A unique key for this particular result expectation
   */
  fun prepareForResult(callerKey: String, resultKey: String) {
    awaitingResults[callerKey to resultKey] = true
  }

  /**
   * Send a result back to a caller.
   *
   * @param callerKey The key of the screen that should receive the result
   * @param resultKey The result key that was used when preparing
   * @param result The result to send
   */
  fun sendResult(callerKey: String, resultKey: String, result: NavResult) {
    val key = callerKey to resultKey
    if (awaitingResults[key] == true) {
      pendingResults[key] = result
    }
  }

  /**
   * Retrieve and consume a pending result.
   *
   * @param callerKey The key of the calling screen
   * @param resultKey The result key used when preparing
   * @return The result if available, null otherwise
   */
  fun awaitResult(callerKey: String, resultKey: String): NavResult? {
    val key = callerKey to resultKey
    val result = pendingResults.remove(key)
    if (result != null) {
      awaitingResults.remove(key)
    }
    return result
  }

  /**
   * Cancel awaiting a result and clean up any pending results.
   *
   * Called automatically by [rememberResultNavigator] when the calling screen leaves composition
   * (e.g., removed from backstack via [Navigator.resetRoot]). This prevents memory leaks from
   * orphaned entries.
   *
   * @param callerKey The key of the screen that was expecting a result
   * @param resultKey The result key that was used when preparing
   */
  fun cancelAwait(callerKey: String, resultKey: String) {
    val key = callerKey to resultKey
    awaitingResults.remove(key)
    pendingResults.remove(key)
  }

  companion object {
    /**
     * Special result key used for results passed via [Navigator.pop].
     *
     * This constant is used internally to route results from popped screens back to their callers.
     */
    const val RESULT_KEY_FROM_POP = "pop_result"
  }
}

/**
 * Create and remember a [NavigationResultHandler] instance with state preservation.
 *
 * This is an internal function called by [rememberNavigator]. Users don't need to call this
 * directly.
 *
 * Note: Pending results are not persisted across process death - they are transient by design since
 * the navigation state would also be restored.
 *
 * @return A remembered [NavigationResultHandler] instance
 */
@Composable
internal fun rememberNavigationResultHandler(): NavigationResultHandler {
  return rememberSaveable(saver = resultHandlerSaver()) { NavigationResultHandler() }
}

private fun resultHandlerSaver(): Saver<NavigationResultHandler, Any> =
  Saver(
    save = {
      // We don't persist results across process death - they're transient
      // Just return empty state marker
      true
    },
    restore = { NavigationResultHandler() },
  )
