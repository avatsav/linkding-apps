package dev.avatsav.linkding.ui.tags

import androidx.compose.runtime.Composable
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.internal.rememberStableCoroutineScope
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import dev.avatsav.linkding.Logger
import dev.avatsav.linkding.ui.TagsScreen
import kotlinx.collections.immutable.toImmutableList
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class TagsUiPresenterFactory(
    private val presenterFactory: (Navigator, TagsScreen) -> TagsPresenter,
) : Presenter.Factory {
    override fun create(
        screen: Screen,
        navigator: Navigator,
        context: CircuitContext,
    ): Presenter<*>? {
        return when (screen) {
            is TagsScreen -> presenterFactory(navigator, screen)
            else -> null
        }
    }
}

@Inject
class TagsPresenter(
    @Assisted private val navigator: Navigator,
    @Assisted private val screen: TagsScreen,
    private val logger: Logger,
) : Presenter<TagsUiState> {

    @Composable
    override fun present(): TagsUiState {
        val coroutineScope = rememberStableCoroutineScope()
        // TODO: Load tags from network
        return TagsUiState(
            selectedTags = screen.selectedTags,
            tags = tags,
        ) { event ->
            when (event) {
                TagsUiEvent.Close -> navigator.pop(TagsScreen.Result.Dismissed)
                is TagsUiEvent.SelectTag -> navigator.pop(TagsScreen.Result.Selected(event.tag))
            }
        }
    }
}

private val tags = mutableListOf<String>().also { tags ->
    repeat(50) {
        tags.add("HELLO ${it + 1}")
    }
}.toImmutableList()
