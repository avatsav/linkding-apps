package dev.avatsav.linkding.ui

import androidx.compose.runtime.Composable
import com.slack.circuit.foundation.Circuit
import dev.avatsav.linkding.CircuitInstance
import dev.avatsav.linkding.inject.Named
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

typealias AuthenticatedContent = @Composable (@Composable (Circuit) -> Unit) -> Unit

@Inject
@Composable
fun AuthenticatedContent(
    @Named(CircuitInstance.AUTHENTICATED) circuit: Circuit,
    @Assisted content: @Composable (Circuit) -> Unit,
) {
    content(circuit)
}
