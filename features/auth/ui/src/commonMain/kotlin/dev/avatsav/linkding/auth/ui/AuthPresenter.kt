package dev.avatsav.linkding.auth.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.github.michaelbull.result.onFailure
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.presenter.Presenter
import dev.avatsav.linkding.auth.ui.usecase.Authenticate
import dev.avatsav.linkding.data.model.AuthError.InvalidApiKey
import dev.avatsav.linkding.data.model.AuthError.InvalidHostname
import dev.avatsav.linkding.data.model.AuthError.Other
import dev.avatsav.linkding.inject.UiScope
import dev.avatsav.linkding.ui.AuthScreen
import me.tatarka.inject.annotations.Inject
import kotlinx.coroutines.launch

@CircuitInject(AuthScreen::class, UiScope::class)
class AuthPresenter @Inject constructor(
    private val authenticate: Authenticate,
) : Presenter<AuthUiState> {

    @Composable
    override fun present(): AuthUiState {
        val scope = rememberCoroutineScope()

        val verifying by authenticate.inProgress.collectAsState(false)

        var invalidHostUrl by rememberSaveable { mutableStateOf(false) }
        var invalidApiKey by rememberSaveable { mutableStateOf(false) }
        var errorMessage by rememberSaveable { mutableStateOf("") }

        return AuthUiState(
            verifying = verifying,
            invalidHostUrl = invalidHostUrl,
            invalidApiKey = invalidApiKey,
            errorMessage = errorMessage,
        ) { event ->
            when (event) {
                is AuthUiEvent.SaveCredentials -> {
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
                        }
                    }
                }
            }
        }
    }
}
