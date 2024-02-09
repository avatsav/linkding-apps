package dev.avatsav.linkding.ui.add

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import dev.avatsav.linkding.Logger
import dev.avatsav.linkding.data.model.SaveBookmark
import dev.avatsav.linkding.domain.interactors.AddBookmark
import dev.avatsav.linkding.ui.AddBookmarkScreen
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class AddBookmarkPresenterFactory(
    private val presenterFactory: (Navigator, String?) -> AddBookmarkPresenter,
) : Presenter.Factory {
    override fun create(
        screen: Screen,
        navigator: Navigator,
        context: CircuitContext,
    ): Presenter<*>? {
        return when (screen) {
            is AddBookmarkScreen -> presenterFactory(navigator, screen.sharedUrl)
            else -> null
        }
    }
}

@Inject
class AddBookmarkPresenter(
    @Assisted private val navigator: Navigator,
    @Assisted private val sharedUrl: String?,
    private val addBookmark: AddBookmark,
    private val logger: Logger,
) : Presenter<AddBookmarkUiState> {

    @Composable
    override fun present(): AddBookmarkUiState {
        val scope = rememberCoroutineScope()

        fun eventSink(event: AddBookmarkUiEvent) {
            when (event) {
                AddBookmarkUiEvent.Close -> navigator.pop()
                is AddBookmarkUiEvent.Save -> scope.launch {
                    addBookmark(
                        SaveBookmark(
                            event.url,
                            event.title,
                            event.description,
                            event.tags.toSet(),
                        ),
                    ).onSuccess {
                        navigator.pop()
                    }.onFailure {
                        TODO()
                    }
                }
            }
        }

        return AddBookmarkUiState(
            sharedUrl = null,
            eventSink = ::eventSink,
        )
    }
}
