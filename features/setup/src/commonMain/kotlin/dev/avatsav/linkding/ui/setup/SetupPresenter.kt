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
import com.r0adkll.kimchi.circuit.annotations.CircuitInject
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.avatsav.linkding.Logger
import dev.avatsav.linkding.data.model.ApiConfig
import dev.avatsav.linkding.data.model.AuthError.InvalidApiKey
import dev.avatsav.linkding.data.model.AuthError.InvalidHostname
import dev.avatsav.linkding.data.model.AuthError.Other
import dev.avatsav.linkding.domain.interactors.Authenticate
import dev.avatsav.linkding.inject.UiScope
import dev.avatsav.linkding.prefs.AppPreferences
import dev.avatsav.linkding.ui.SetupScreen
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import kotlinx.coroutines.launch

@CircuitInject(SetupScreen::class, UiScope::class)
@Inject
class SetupPresenter(
    @Assisted private val navigator: Navigator,
    private val authenticate: Authenticate,
    private val appPreferences: AppPreferences,
    private val logger: Logger,
) : Presenter<SetupUiState> {

    @Composable
    override fun present(): SetupUiState {
        val scope = rememberCoroutineScope()

        val verifying by authenticate.inProgress.collectAsState(false)

        var invalidHostUrl by rememberSaveable { mutableStateOf(false) }
        var invalidApiKey by rememberSaveable { mutableStateOf(false) }
        var errorMessage by rememberSaveable { mutableStateOf("") }

        return SetupUiState(
            verifying = verifying,
            invalidHostUrl = invalidHostUrl,
            invalidApiKey = invalidApiKey,
            errorMessage = errorMessage,
        ) { event ->
            when (event) {
                is SetupUiEvent.SaveConfiguration -> {
                    invalidHostUrl = false
                    invalidApiKey = false
                    errorMessage = ""
                    scope.launch {
                        authenticate(
                            Authenticate.Param(
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
                            appPreferences.setApiConfig(ApiConfig(event.hostUrl, event.apiKey))
                        }
                    }
                }
            }
        }
    }
}
