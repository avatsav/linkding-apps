package dev.avatsav.linkding.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import dev.avatsav.linkding.navigation.impl.ResultEventBusImpl
import kotlinx.coroutines.flow.Flow

/**
 * An EventBus for passing results between multiple sets of screens.
 *
 * It provides a solution for event based results.
 */
interface ResultEventBus {

  /** Provides a flow for the given resultKey. Creates the channel if it doesn't exist. */
  fun getResultFlow(resultKey: String): Flow<Any?>

  /** Sends a result into the channel associated with the given resultKey. */
  fun sendResult(resultKey: String, result: Any?)

  /** Removes and closes the channel associated with the given key. */
  fun removeResult(resultKey: String)
}

/** Creates and remembers a [ResultEventBus] instance. */
@Composable
fun rememberResultEventBus(): ResultEventBus {
  return remember { ResultEventBusImpl() }
}

/** Sends a result using the type's class name as the key. */
inline fun <reified T> ResultEventBus.sendResult(result: T) {
  sendResult(T::class.toString(), result)
}

/** Removes result channel using the type's class name as the key. */
inline fun <reified T> ResultEventBus.removeResult() {
  removeResult(T::class.toString())
}

/**
 * An Effect to receive results from other screens via the [ResultEventBus].
 *
 * The channel is created eagerly when this effect starts, ensuring the collector is always active.
 * The channel is cleaned up when the composable leaves composition to prevent memory leaks.
 *
 * @param resultEventBus the ResultEventBus to retrieve the result from. The default value is read
 *   from the `LocalResultEventBus` composition local.
 * @param resultKey the key that should be associated with this effect
 * @param onResult the callback to invoke when a result is received
 */
@Composable
inline fun <reified T> ResultEffect(
  resultEventBus: ResultEventBus = LocalResultEventBus.current,
  resultKey: String = T::class.toString(),
  crossinline onResult: suspend (T) -> Unit,
) {
  DisposableEffect(resultKey, resultEventBus) {
    onDispose { resultEventBus.removeResult(resultKey) }
  }

  LaunchedEffect(resultKey, resultEventBus) {
    resultEventBus.getResultFlow(resultKey).collect { result ->
      @Suppress("UNCHECKED_CAST") onResult.invoke(result as T)
    }
  }
}
