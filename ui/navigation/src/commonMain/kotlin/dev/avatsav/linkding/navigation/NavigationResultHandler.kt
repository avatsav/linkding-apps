package dev.avatsav.linkding.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateMap

/**
 * Handles result passing between screens using screen keys.
 *
 * Results are stored temporarily and retrieved when the calling screen becomes active again. Uses a
 * (callerKey, resultKey) pair to route results to the correct destination.
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

  /** Cancel awaiting a result. */
  fun cancelAwait(callerKey: String, resultKey: String) {
    val key = callerKey to resultKey
    awaitingResults.remove(key)
    pendingResults.remove(key)
  }

  companion object Companion {
    /** Special result key used when popping with a result directly. */
    const val RESULT_KEY_FROM_POP = "_pop_result_"
  }
}

/** Remember an [NavigationResultHandler] with state preservation. */
@Composable
fun rememberNavigationResultHandler(): NavigationResultHandler {
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
