package dev.avatsav.linkding.auth.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.github.michaelbull.result.onErr
import com.github.michaelbull.result.onOk
import dev.avatsav.linkding.auth.ui.usecase.Authenticate
import dev.avatsav.linkding.data.model.AuthError.InvalidApiKey
import dev.avatsav.linkding.data.model.AuthError.InvalidHostname
import dev.avatsav.linkding.data.model.AuthError.Other
import dev.avatsav.linkding.internet.LocalNetworkAccess
import dev.avatsav.linkding.presenter.MoleculePresenter
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Inject
class AuthPresenter(
    private val authenticate: Authenticate,
    private val localNetworkAccess: LocalNetworkAccess,
) : MoleculePresenter<AuthUiEvent, AuthUiState, AuthEffect>() {

  @Composable
  override fun models(events: Flow<AuthUiEvent>): AuthUiState {
    val verifying by authenticate.inProgress.collectAsState(false)
    var checkingAccess by remember { mutableStateOf(false) }
    var awaitingPermission by remember { mutableStateOf(false) }
    var pendingCredentials by remember { mutableStateOf<AuthUiEvent.SaveCredentials?>(null) }
    var localNetworkAccessRequired by rememberSaveable { mutableStateOf(false) }
    var invalidHostUrl by rememberSaveable { mutableStateOf(false) }
    var invalidApiKey by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf("") }

    ObserveEvents { event ->
      when (event) {
        is AuthUiEvent.SaveCredentials -> {
          if (!checkingAccess && !awaitingPermission && !verifying) {
            invalidHostUrl = false
            invalidApiKey = false
            errorMessage = ""
            localNetworkAccessRequired = false
            checkingAccess = true
            pendingCredentials = event
            presenterScope.launch {
              try {
                if (localNetworkAccess.check(event.hostUrl) == LocalNetworkAccess.State.Required) {
                  localNetworkAccessRequired = true
                  awaitingPermission = true
                  emitEffect(AuthEffect.RequestLocalNetworkAccess)
                } else {
                  authenticate(Authenticate.Param(event.hostUrl, event.apiKey))
                      .onOk { emitEffect(AuthEffect.AuthenticationSuccess) }
                      .onErr { error ->
                        when (error) {
                          is InvalidApiKey -> invalidApiKey = true
                          is InvalidHostname -> invalidHostUrl = true
                          is Other -> errorMessage = error.message
                        }
                      }
                  pendingCredentials = null
                }
              } finally {
                checkingAccess = false
              }
            }
          }
        }
        is AuthUiEvent.LocalNetworkPermissionResult -> {
          localNetworkAccess.refresh()
          awaitingPermission = false
          localNetworkAccessRequired = !event.granted
          val credentials = pendingCredentials
          pendingCredentials = null
          if (event.granted && credentials != null) eventSink(credentials)
        }
      }
    }

    return AuthUiState(
        verifying = verifying || checkingAccess || awaitingPermission,
        invalidHostUrl = invalidHostUrl,
        invalidApiKey = invalidApiKey,
        errorMessage = errorMessage,
        localNetworkAccessRequired = localNetworkAccessRequired,
    )
  }
}
