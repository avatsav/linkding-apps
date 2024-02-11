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
import dev.avatsav.linkding.data.model.CheckUrlResult
import dev.avatsav.linkding.data.model.SaveBookmark
import dev.avatsav.linkding.domain.interactors.AddBookmark
import dev.avatsav.linkding.domain.interactors.CheckBookmarkUrl
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
    private val checkBookmarkUrl: CheckBookmarkUrl,
    private val logger: Logger,
) : Presenter<AddBookmarkUiState> {

    @Composable
    override fun present(): AddBookmarkUiState {
        val scope = rememberCoroutineScope()

        var checkUrlResult: CheckUrlResult? by rememberSaveable { mutableStateOf(null) }
        var errorMessage by rememberSaveable { mutableStateOf("") }

        val checkingUrl by checkBookmarkUrl.inProgress.collectAsState(false)
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

                is AddBookmarkUiEvent.CheckUrl -> scope.launch {
                    checkBookmarkUrl(event.url).onSuccess {
                        checkUrlResult = it
                    }.onFailure {
                        logger.e { "CheckError: $it" }
                    }
                }
            }
        }

        return AddBookmarkUiState(
            sharedUrl = sharedUrl,
            checkingUrl = checkingUrl,
            checkUrlResult = checkUrlResult,
            saving = saving,
            errorMessage = errorMessage,
            eventSink = ::eventSink,
        )
    }
}
