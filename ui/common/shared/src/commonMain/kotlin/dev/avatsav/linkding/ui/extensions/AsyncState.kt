package dev.avatsav.linkding.ui.extensions

import androidx.compose.runtime.Composable
import dev.avatsav.linkding.ui.AsyncState
import dev.avatsav.linkding.ui.Fail
import dev.avatsav.linkding.ui.Loading
import dev.avatsav.linkding.ui.Success

@Composable
inline infix fun <V : Any, E : Any> AsyncState<V, E>.composableOnSuccess(
    transform: @Composable (V) -> (@Composable (() -> Unit)?),
): (@Composable () -> Unit)? {
    if (this is Success) {
        return transform(value)
    }
    return null
}

@Composable
inline infix fun <V : Any, E : Any> AsyncState<V, E>.composableOnLoading(
    transform: @Composable () -> (@Composable (() -> Unit)?),
): (@Composable () -> Unit)? {
    if (this is Loading) {
        return transform()
    }
    return null
}

@Composable
inline infix fun <V : Any, E : Any> AsyncState<V, E>.composableOnFail(
    transform: @Composable (E) -> (@Composable (() -> Unit)?),
): (@Composable () -> Unit)? {
    if (this is Fail) {
        return transform(error)
    }
    return null
}
