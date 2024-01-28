package dev.avatsav.linkding.ui.setup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.avatsav.linkding.data.model.ApiConfiguration
import dev.avatsav.linkding.domain.interactors.SaveApiConfiguration
import dev.avatsav.linkding.domain.interactors.VerifyApiConfiguration
import dev.avatsav.linkding.ui.BookmarkListScreen
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import org.jetbrains.skia.skottie.Logger

@Inject
class SetupPresenter(
    @Assisted private val navigator: Navigator,
    private val verifyApiConfiguration: VerifyApiConfiguration,
    private val saveApiConfiguration: SaveApiConfiguration,
    val logger: Logger,
) : Presenter<SetupUiState> {

    @Composable
    override fun present(): SetupUiState {
        val scope = rememberCoroutineScope()
        val verifying by verifyApiConfiguration.inProgress.collectAsState(false)
        val saving by saveApiConfiguration.inProgress.collectAsState(false)
        var errorMessage by rememberSaveable { mutableStateOf("") }

        fun eventSink(event: SetupUiEvent) {
            when (event) {
                is SetupUiEvent.SaveConfiguration -> {
                    scope.launch {
                        verifyApiConfiguration(
                            VerifyApiConfiguration.Param(
                                event.hostUrl,
                                event.apiKey,
                            ),
                        ).onFailure {
                            errorMessage = it.message
                        }.onSuccess {
                            saveApiConfiguration(
                                ApiConfiguration.Linkding(
                                    event.hostUrl,
                                    event.apiKey,
                                ),
                            ).onSuccess {
                                navigator.goTo(BookmarkListScreen)
                                navigator.resetRoot(BookmarkListScreen)
                            }.onFailure {
                                errorMessage = it.message
                            }
                        }
                    }
                }
            }
        }
        return SetupUiState(
            verifying = verifying,
            saving = saving,
            eventSink = ::eventSink,
        )
    }


}
