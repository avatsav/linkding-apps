package dev.avatsav.linkding.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.RememberObserver
import com.slack.circuit.retained.rememberRetained
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel

/**
 * https://chrisbanes.me/posts/retaining-beyond-viewmodels/
 * https://github.com/chrisbanes/tivi/blob/b771f2a6f69f73f761ab416c57f0761a3537c15a/common/ui/compose/src/commonMain/kotlin/app/tivi/common/compose/CoroutineScope.kt#L32C5-L46
 */
@Composable
fun rememberRetainedCoroutineScope(): CoroutineScope {
    return rememberRetained("coroutine_scope") {
        object : RememberObserver {
            val scope = CoroutineScope(Dispatchers.Main + Job())
            override fun onAbandoned() = onForgotten()
            override fun onForgotten() = scope.cancel()
            override fun onRemembered() = Unit
        }
    }.scope
}
