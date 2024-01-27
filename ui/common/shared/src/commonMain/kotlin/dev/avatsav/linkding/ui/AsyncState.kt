package dev.avatsav.linkding.ui

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

// Inspired by : https://github.com/airbnb/mavericks/blob/main/mvrx-common/src/main/java/com/airbnb/mvrx/Async.kt
// and https://github.com/michaelbull/kotlin-result

sealed class AsyncState<out V : Any, out E : Any>(
    open val complete: Boolean,
    val shouldLoad: Boolean,
    private val value: V?,
) {
    open operator fun invoke(): V? = value
}

object Uninitialized :
    AsyncState<Nothing, Nothing>(complete = false, shouldLoad = true, value = null)

data class Loading<out V : Any>(val value: V? = null) :
    AsyncState<V, Nothing>(complete = false, shouldLoad = false, value = value)

abstract class Success<out V : Any>(
    open val value: V,
) : AsyncState<V, Nothing>(complete = true, shouldLoad = false, value = value) {

    override operator fun invoke(): V = value

    companion object {
        fun <V : Any> content(value: V): Content<V> {
            return Content(value)
        }

        fun <V : Any> pagedContent(value: V, pageStatus: PageStatus): PagedContent<V> {
            return PagedContent(value, pageStatus)
        }
    }
}

data class Content<out V : Any>(
    override val value: V,
) : Success<V>(value)

data class PagedContent<out V : Any>(
    override val value: V,
    val status: PageStatus,
) : Success<V>(value)

enum class PageStatus {
    HasMore, Complete, LoadingMore, ErrorLoadingMore
}

data class Fail<out E : Any>(
    val error: E,
) : AsyncState<Nothing, E>(complete = true, shouldLoad = true, value = null)

@OptIn(ExperimentalContracts::class)
inline infix fun <V : Any, E : Any> AsyncState<V, E>.onSuccess(action: (V) -> Unit): AsyncState<V, E> {
    contract { callsInPlace(action, InvocationKind.AT_MOST_ONCE) }
    if (this is Success) {
        action(value)
    }
    return this
}

@OptIn(ExperimentalContracts::class)
inline infix fun <V : Any, E : Any> AsyncState<V, E>.onContent(action: (Content<V>) -> Unit): AsyncState<V, E> {
    contract { callsInPlace(action, InvocationKind.AT_MOST_ONCE) }
    if (this is Content) {
        action(this)
    }
    return this
}

@OptIn(ExperimentalContracts::class)
inline infix fun <V : Any, E : Any> AsyncState<V, E>.onPagedContent(action: (PagedContent<V>) -> Unit): AsyncState<V, E> {
    contract { callsInPlace(action, InvocationKind.AT_MOST_ONCE) }
    if (this is PagedContent) {
        action(this)
    }
    return this
}

@OptIn(ExperimentalContracts::class)
inline infix fun <V : Any, E : Any> AsyncState<V, E>.onLoading(action: () -> Unit): AsyncState<V, E> {
    contract { callsInPlace(action, InvocationKind.AT_MOST_ONCE) }
    if (this is Loading) {
        action()
    }
    return this
}

@OptIn(ExperimentalContracts::class)
inline infix fun <V : Any, E : Any> AsyncState<V, E>.onFail(action: (E) -> Unit): AsyncState<V, E> {
    contract { callsInPlace(action, InvocationKind.AT_MOST_ONCE) }
    if (this is Fail) {
        action(error)
    }
    return this
}

@OptIn(ExperimentalContracts::class)
fun <V : Any, E : Any> AsyncState<V, E>.get(): V? {
    contract {
        returnsNotNull() implies (this@get is Success<V>)
        returns(null) implies (this@get is Fail<E>)
        returns(null) implies (this@get is Loading)
        returns(null) implies (this@get is Uninitialized)
    }
    return when (this) {
        is Loading -> null
        is Success -> value
        is Fail -> null
        Uninitialized -> null
    }
}

@OptIn(ExperimentalContracts::class)
fun <V : Any, E : Any> AsyncState<V, E>.getError(): E? {
    contract {
        returnsNotNull() implies (this@getError is Fail<E>)
        returns(null) implies (this@getError is Success<V>)
        returns(null) implies (this@getError is Loading)
        returns(null) implies (this@getError is Uninitialized)
    }
    return when (this) {
        is Loading -> null
        is Success -> null
        is Fail -> error
        Uninitialized -> null
    }
}
