package dev.avatsav.linkding.auth.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.github.michaelbull.result.onFailure
import dev.avatsav.linkding.auth.ui.usecase.Authenticate
import dev.avatsav.linkding.data.model.AuthError.InvalidApiKey
import dev.avatsav.linkding.data.model.AuthError.InvalidHostname
import dev.avatsav.linkding.data.model.AuthError.Other
import dev.avatsav.linkding.viewmodel.MoleculeViewModel
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Inject
class AuthViewModel(private val authenticate: Authenticate) :
  MoleculeViewModel<AuthUiEvent, AuthUiState>() {

  @Composable
  override fun models(events: Flow<AuthUiEvent>): AuthUiState {
    val verifying by authenticate.inProgress.collectAsState(false)
    var invalidHostUrl by rememberSaveable { mutableStateOf(false) }
    var invalidApiKey by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf("") }

    CollectEvents { event ->
      when (event) {
        is AuthUiEvent.SaveCredentials -> {
          invalidHostUrl = false
          invalidApiKey = false
          errorMessage = ""
          viewModelScope.launch {
            authenticate(Authenticate.Param(event.hostUrl, event.apiKey)).onFailure { error ->
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

    return AuthUiState(
      verifying = verifying,
      invalidHostUrl = invalidHostUrl,
      invalidApiKey = invalidApiKey,
      errorMessage = errorMessage,
    )
  }
}
