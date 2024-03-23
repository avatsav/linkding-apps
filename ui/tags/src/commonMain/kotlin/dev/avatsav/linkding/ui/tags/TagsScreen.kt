package dev.avatsav.linkding.ui.tags

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.ui.ui
import dev.avatsav.linkding.ui.TagsScreen
import me.tatarka.inject.annotations.Inject

@Inject
class TagsUiFactory : Ui.Factory {
    override fun create(screen: Screen, context: CircuitContext): Ui<*>? {
        return when (screen) {
            is TagsScreen -> {
                ui<TagsUiState> { state, modifier ->
                    Tags(state, modifier)
                }
            }

            else -> null
        }
    }
}

@Composable
fun Tags(
    state: TagsUiState,
    modifier: Modifier = Modifier,
) {
    // https://issuetracker.google.com/issues/256100927#comment1
    val eventSink = state.eventSink

}
