package dev.avatsav.linkding.ui

import arrow.core.Either
import arrow.core.Validated
import arrow.core.continuations.either
import arrow.core.invalid
import arrow.core.valid
import dev.avatsav.linkding.Presenter
import dev.avatsav.linkding.data.Credentials
import dev.avatsav.linkding.data.CredentialsStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SetupPresenter(private val credentialsStore: CredentialsStore) : Presenter() {

    private val _uiState = MutableStateFlow(ViewState())
    val uiState: StateFlow<ViewState> = _uiState

    fun setCredentials(url: String, apiKey: String) {
        presenterScope.launch {
            _uiState.emit(_uiState.value.copy(loading = true))
            ValidateInput(url, apiKey).map { credentials ->
                withContext(Dispatchers.Default) {
                    credentialsStore.set(credentials)
                }
                _uiState.emit(_uiState.value.copy(loading = false, error = ViewState.Error.None))
            }.mapLeft { error ->
                _uiState.emit((_uiState.value.copy(loading = false, error = error)))
            }
        }
    }

    data class ViewState(val loading: Boolean = false, val error: Error = Error.None) {
        companion object {
            val Initial = ViewState(
                loading = false, error = Error.None
            )
        }

        sealed class Error(val message: String = "") {
            object None : Error()
            object UrlEmpty : Error("URL cannot be empty")
            object ApiKeyEmpty : Error("Api Key cannot be empty")
        }
    }

    object ValidateInput {
        private fun Credentials.urlNotEmpty(): Validated<ViewState.Error, Credentials> =
            if (url.isNotEmpty()) valid()
            else ViewState.Error.UrlEmpty.invalid()

        private fun Credentials.apiKeyNotEmpty(): Validated<ViewState.Error, Credentials> =
            if (apiKey.isNotEmpty()) valid()
            else ViewState.Error.ApiKeyEmpty.invalid()

        private fun Credentials.validate(): Either<ViewState.Error, Credentials> = either.eager {
            urlNotEmpty().bind()
            apiKeyNotEmpty().bind()
            Credentials(url, apiKey)
        }

        operator fun invoke(url: String, apiKey: String): Either<ViewState.Error, Credentials> {
            return Credentials(url, apiKey).validate()
        }

    }
}



