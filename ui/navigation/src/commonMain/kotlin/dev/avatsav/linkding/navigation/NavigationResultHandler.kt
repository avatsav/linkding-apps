package dev.avatsav.linkding.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateMap

/** Internal handler for routing results between routes. Used by [rememberResultNavigator]. */
@Stable
class NavigationResultHandler
internal constructor(
  private val pendingResults: SnapshotStateMap<Pair<String, String>, NavResult> =
    mutableStateMapOf(),
  private val awaitingResults: SnapshotStateMap<Pair<String, String>, Boolean> = mutableStateMapOf(),
) {
  /** Register that a route is expecting a result. */
  fun prepareForResult(callerKey: String, resultKey: String) {
    awaitingResults[callerKey to resultKey] = true
  }

  /** Send a result back to a caller. */
  fun sendResult(callerKey: String, resultKey: String, result: NavResult) {
    val key = callerKey to resultKey
    if (awaitingResults[key] == true) {
      pendingResults[key] = result
    }
  }

  /** Retrieve and consume a pending result. */
  fun awaitResult(callerKey: String, resultKey: String): NavResult? {
    val key = callerKey to resultKey
    val result = pendingResults.remove(key)
    if (result != null) {
      awaitingResults.remove(key)
    }
    return result
  }

  /** Cancel awaiting a result and clean up. */
  fun cancelAwait(callerKey: String, resultKey: String) {
    val key = callerKey to resultKey
    awaitingResults.remove(key)
    pendingResults.remove(key)
  }

  companion object {
    /** Result key used for results passed via [Navigator.pop]. */
    const val RESULT_KEY_FROM_POP = "pop_result"
  }
}

/** Internal: Create and remember a [NavigationResultHandler]. */
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
