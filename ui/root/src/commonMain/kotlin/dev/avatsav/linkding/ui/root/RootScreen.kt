package dev.avatsav.linkding.ui.root

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
    Scaffold(
        modifier = modifier,
        content = { padding ->
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.surface)
                    .fillMaxSize(),
            ) {
                CircularProgressIndicator()
            }
        },
    )
}
