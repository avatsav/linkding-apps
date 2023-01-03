package dev.avatsav.linkding.android.debug

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import dev.avatsav.linkding.android.BuildConfig
import timber.log.Timber

/**
 * https://github.com/chrisbanes/tivi/blob/main/common/ui/compose/src/main/java/app/tivi/common/compose/Debug.kt
 */

class Ref(var value: Int)

const val EnableDebugCompositionLogs = true

/**
 * An effect which logs the number compositions at the invoked point of the slot table.
 * Thanks to [objcode](https://github.com/objcode) for this code.
 *
 * This is an inline function to act as like a C-style #include to the host composable function.
 * That way we track it's compositions, not this function's compositions.
 *
 * @param tag Log tag used for [Log.d]
 */
@Composable
inline fun LogCompositions(tag: String) {
    if (EnableDebugCompositionLogs && BuildConfig.DEBUG) {
        val ref = remember { Ref(0) }
        SideEffect { ref.value++ }
        Timber.tag(tag)
        Timber.w("Compositions: ${ref.value}")
    }
}