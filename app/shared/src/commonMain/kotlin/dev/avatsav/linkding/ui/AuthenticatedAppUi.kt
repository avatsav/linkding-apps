package dev.avatsav.linkding.ui

import androidx.compose.runtime.Composable
import com.slack.circuit.foundation.Circuit
import dev.avatsav.linkding.CircuitInstance
import dev.avatsav.linkding.inject.Named
import me.tatarka.inject.annotations.Inject

interface AuthenticatedAppUi {
    @Composable
    fun Content(content: @Composable (Circuit) -> Unit)
}

@Inject
class DefaultAuthenticatedAppUi(@Named(CircuitInstance.AUTHENTICATED) private val circuit: Circuit) : AuthenticatedAppUi {
    @Composable
    override fun Content(content: @Composable (Circuit) -> Unit) {
        content(circuit)
    }
}
