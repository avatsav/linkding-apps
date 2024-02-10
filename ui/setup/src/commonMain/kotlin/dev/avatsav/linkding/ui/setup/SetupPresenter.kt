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
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import dev.avatsav.linkding.Logger
import dev.avatsav.linkding.data.model.ApiConfig
import dev.avatsav.linkding.data.model.ConfigurationError.InvalidApiKey
import dev.avatsav.linkding.data.model.ConfigurationError.InvalidHostname
import dev.avatsav.linkding.data.model.ConfigurationError.Other
import dev.avatsav.linkding.domain.interactors.SaveApiConfiguration
import dev.avatsav.linkding.domain.interactors.VerifyApiConfiguration
import dev.avatsav.linkding.ui.BookmarksScreen
import dev.avatsav.linkding.ui.SetupScreen
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class SetupUiPresenterFactory(
    private val presenterFactory: (Navigator) -> SetupPresenter,
) : Presenter.Factory {
    override fun create(
        screen: Screen,
        navigator: Navigator,
        context: CircuitContext,
    ): Presenter<*>? {
        return when (screen) {
            is SetupScreen -> presenterFactory(navigator)
            else -> null
        }
    }
}

@Inject
class SetupPresenter(
    @Assisted private val navigator: Navigator,
    private val verifyApiConfiguration: VerifyApiConfiguration,
    private val saveApiConfiguration: SaveApiConfiguration,
    private val logger: Logger,
) : Presenter<SetupUiState> {

    @Composable
    override fun present(): SetupUiState {
        val scope = rememberCoroutineScope()

        val verifying by verifyApiConfiguration.inProgress.collectAsState(false)
        val saving by saveApiConfiguration.inProgress.collectAsState(false)

        var invalidHostUrl by rememberSaveable { mutableStateOf(false) }
        var invalidApiKey by rememberSaveable { mutableStateOf(false) }
        var errorMessage by rememberSaveable { mutableStateOf("") }

        fun eventSink(event: SetupUiEvent) = when (event) {
            is SetupUiEvent.SaveConfiguration -> {
                invalidHostUrl = false
                invalidApiKey = false
                errorMessage = ""
                scope.launch {
                    verifyApiConfiguration(
                        VerifyApiConfiguration.Param(
                            event.hostUrl,
                            event.apiKey,
                        ),
                    ).onFailure { error ->
                        when (error) {
                            is InvalidApiKey -> invalidApiKey = true
                            is InvalidHostname -> invalidHostUrl = true
                            is Other -> errorMessage = error.message
                        }
                    }.onSuccess {
                        saveApiConfiguration(
                            ApiConfig(event.hostUrl, event.apiKey),
                        ).onSuccess {
                            navigator.goTo(BookmarksScreen)
                            navigator.resetRoot(BookmarksScreen)
                        }.onFailure {
                            errorMessage = it
                        }
                    }
                }
            }
        }

        return SetupUiState(
            verifying = verifying,
            saving = saving,
            invalidHostUrl = invalidHostUrl,
            invalidApiKey = invalidApiKey,
            errorMessage = errorMessage,
            eventSink = ::eventSink,
        )
    }
}
