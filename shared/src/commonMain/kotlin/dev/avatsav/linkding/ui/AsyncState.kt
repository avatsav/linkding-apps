package dev.avatsav.linkding.ui

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

// Inspired by : https://github.com/airbnb/mavericks/blob/main/mvrx-common/src/main/java/com/airbnb/mvrx/Async.kt

sealed class AsyncState<out V, out E>(
    val complete: Boolean, val shouldLoad: Boolean, private val value: V?,
) {
    open operator fun invoke(): V? = value
}

object Uninitialized :
    AsyncState<Nothing, Nothing>(complete = false, shouldLoad = true, value = null)

data class Loading<out V>(val value: V? = null) :
    AsyncState<V, Nothing>(complete = false, shouldLoad = false, value = value)

data class Success<out V>(val value: V) :
    AsyncState<V, Nothing>(complete = true, shouldLoad = false, value = value) {
    override operator fun invoke(): V = value
}

data class Fail<out E>(
    val error: E,
) : AsyncState<Nothing, E>(complete = true, shouldLoad = true, value = null)


@OptIn(ExperimentalContracts::class)
inline infix fun <V, E> AsyncState<V, E>.onSuccess(action: (V) -> Unit): AsyncState<V, E> {
    contract { callsInPlace(action, InvocationKind.AT_MOST_ONCE) }
    if (this is Success) {
        action(value)
    }
    return this
}

@OptIn(ExperimentalContracts::class)
inline infix fun <V, E> AsyncState<V, E>.onLoading(action: () -> Unit): AsyncState<V, E> {
    contract { callsInPlace(action, InvocationKind.AT_MOST_ONCE) }
    if (this is Loading) {
        action()
    }
    return this
}

@OptIn(ExperimentalContracts::class)
inline infix fun <V, E> AsyncState<V, E>.onFail(action: (E) -> Unit): AsyncState<V, E> {
    contract { callsInPlace(action, InvocationKind.AT_MOST_ONCE) }
    if (this is Fail) {
        action(error)
    }
    return this
}
