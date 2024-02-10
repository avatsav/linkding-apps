package dev.avatsav.linkding.ui.add

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import dev.avatsav.linkding.Logger
import dev.avatsav.linkding.data.model.SaveBookmark
import dev.avatsav.linkding.data.model.UnfurlData
import dev.avatsav.linkding.domain.interactors.AddBookmark
import dev.avatsav.linkding.domain.interactors.UnfurlLink
import dev.avatsav.linkding.ui.AddBookmarkScreen
import dev.avatsav.linkding.ui.add.AddBookmarkUiEvent.Close
import dev.avatsav.linkding.ui.add.AddBookmarkUiEvent.Save
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
    private val unfurlLink: UnfurlLink,
    private val logger: Logger,
) : Presenter<AddBookmarkUiState> {

    @Composable
    override fun present(): AddBookmarkUiState {
        val scope = rememberCoroutineScope()

        var unfurlData: UnfurlData? by rememberSaveable { mutableStateOf(null) }
        var errorMessage by rememberSaveable { mutableStateOf("") }

        val unfurling by unfurlLink.inProgress.collectAsState(false)
        val saving by addBookmark.inProgress.collectAsState(false)

        fun eventSink(event: AddBookmarkUiEvent) {
            when (event) {
                Close -> navigator.pop()
                is Save -> scope.launch {
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
                        errorMessage = it.message
                    }
                }

                is AddBookmarkUiEvent.Unfurl -> scope.launch {
                    logger.d { "Starting to unfurl: ${event.url}" }
                    unfurlLink(event.url)
                        .onSuccess {
                            logger.d { "Unfurl success: $it" }
                            unfurlData = it
                        }.onFailure {
                            logger.e { "Unfurl failed: ${event.url}" }
                        }
                }
            }
        }

        return AddBookmarkUiState(
            sharedUrl = sharedUrl,
            unfurling = unfurling,
            unfurlData = unfurlData,
            saving = saving,
            errorMessage = errorMessage,
            eventSink = ::eventSink,
        )
    }
}
