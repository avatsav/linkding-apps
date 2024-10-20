package dev.avatsav.linkding.ui

import androidx.compose.runtime.Composable
import com.slack.circuit.foundation.Circuit
import dev.avatsav.linkding.CircuitInstance
import dev.avatsav.linkding.inject.Named
import dev.avatsav.linkding.inject.UserScope
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

interface AuthenticatedAppUi {
    @Composable
    fun Content(content: @Composable (Circuit) -> Unit)
}

@Inject
@SingleIn(UserScope::class)
@ContributesBinding(UserScope::class)
class DefaultAuthenticatedAppUi(@Named(CircuitInstance.AUTHENTICATED) private val circuit: Circuit) : AuthenticatedAppUi {
    @Composable
    override fun Content(content: @Composable (Circuit) -> Unit) {
        content(circuit)
    }
}
