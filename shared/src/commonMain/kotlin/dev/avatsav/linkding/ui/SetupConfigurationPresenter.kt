package dev.avatsav.linkding.ui

import arrow.core.Either
import arrow.core.Validated
import arrow.core.continuations.either
import arrow.core.invalid
import arrow.core.valid
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import dev.avatsav.linkding.Presenter
import dev.avatsav.linkding.bookmark.application.ports.`in`.BookmarkService
import dev.avatsav.linkding.data.Configuration
import dev.avatsav.linkding.data.ConfigurationStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SetupConfigurationPresenter(
    private val configurationStore: ConfigurationStore, private val bookmarkService: BookmarkService
) : Presenter() {

    private val _uiState = MutableStateFlow(ViewState())

    @NativeCoroutinesState
    val uiState: StateFlow<ViewState> = _uiState

    fun setConfiguration(url: String, apiKey: String) {
        presenterScope.launch {
            _uiState.emit(_uiState.value.copy(loading = true))
            ValidateInput(bookmarkService).invoke(url, apiKey).map { credentials ->
                withContext(Dispatchers.Default) {
                    configurationStore.set(credentials)
                }
                _uiState.emit(
                    _uiState.value.copy(
                        loading = false, error = ViewState.Error.None
                    )
                )
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
            object CannotConnect : Error("Cannot connect.")

        }
    }

    class ValidateInput(private val bookmarkService: BookmarkService) {
        private suspend fun Configuration.testConnection(bookmarkService: BookmarkService): Validated<ViewState.Error.CannotConnect, Configuration> {
            return bookmarkService.testConnection(this).fold(ifLeft = {
                ViewState.Error.CannotConnect.invalid()
            }, ifRight = {
                valid()
            })
        }

        private suspend fun Configuration.validate(): Either<ViewState.Error, Configuration> =
            either {
                ensure(url.isNotEmpty()) { ViewState.Error.UrlEmpty }
                ensure(apiKey.isNotEmpty()) { ViewState.Error.ApiKeyEmpty }
                testConnection(bookmarkService).bind()
                Configuration(url, apiKey)
            }

        suspend operator fun invoke(
            url: String, apiKey: String
        ): Either<ViewState.Error, Configuration> {
            return Configuration(url, apiKey).validate()
        }
    }
}



