package dev.avatsav.linkding.ui.root

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.ui.ui
import dev.avatsav.linkding.ui.RootScreen
import me.tatarka.inject.annotations.Inject

@Inject
class RootUiFactory : Ui.Factory {
    override fun create(screen: Screen, context: CircuitContext): Ui<*>? {
        return when (screen) {
            is RootScreen -> {
                ui<RootUiState> { state, modifier ->
                    RootScreen(state, modifier)
                }
            }

            else -> null
        }
    }
}

@Composable
fun RootScreen(
    state: RootUiState,
    modifier: Modifier,
) {
    // Placeholder screen. The routing to the right workflow is happening in the presenter.
}
