package dev.avatsav.linkding.ui.tags

import androidx.compose.runtime.Composable
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.internal.rememberStableCoroutineScope
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import dev.avatsav.linkding.Logger
import dev.avatsav.linkding.ui.TagsScreen
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class TagsUiPresenterFactory(
    private val presenterFactory: (Navigator) -> TagsPresenter,
) : Presenter.Factory {
    override fun create(
        screen: Screen,
        navigator: Navigator,
        context: CircuitContext,
    ): Presenter<*>? {
        return when (screen) {
            is TagsScreen -> presenterFactory(navigator)
            else -> null
        }
    }
}

@Inject
class TagsPresenter(
    @Assisted private val navigator: Navigator,
    private val logger: Logger,
) : Presenter<TagsUiState> {

    @Composable
    override fun present(): TagsUiState {
        val coroutineScope = rememberStableCoroutineScope()
        return TagsUiState { event ->

        }
    }
}
