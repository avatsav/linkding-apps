package dev.avatsav.linkding.ui.circuit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import com.slack.circuit.retained.rememberRetained
import kotlinx.coroutines.CoroutineScope

@Composable
@NonRestartableComposable
fun RetainedLaunchedEffect(
    vararg inputs: Any?,
    block: suspend CoroutineScope.() -> Unit,
) {
    val latestBlock by rememberUpdatedState(block)
    var launched by rememberRetained(*inputs) { mutableStateOf(false) }
    LaunchedEffect(*inputs) {
        if (launched) return@LaunchedEffect
        launched = true
        latestBlock()
    }
}
