package dev.avatsav.linkding.ui.viewmodel

import arrow.core.Either
import arrow.core.Validated
import arrow.core.continuations.either
import arrow.core.invalid
import arrow.core.valid
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import dev.avatsav.linkding.ViewModel
import dev.avatsav.linkding.bookmark.application.ports.`in`.BookmarkService
import dev.avatsav.linkding.data.Configuration
import dev.avatsav.linkding.data.ConfigurationNotSetup
import dev.avatsav.linkding.data.ConfigurationStore
import dev.avatsav.linkding.data.Setup
import dev.avatsav.linkding.ui.AsyncState
import dev.avatsav.linkding.ui.Content
import dev.avatsav.linkding.ui.Fail
import dev.avatsav.linkding.ui.Loading
import dev.avatsav.linkding.ui.Uninitialized
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class HomeViewModel(
    private val configurationStore: ConfigurationStore, private val bookmarkService: BookmarkService
) : ViewModel() {

    private val setupStateFlow: Flow<AsyncState<Configuration, HomeViewState.NotSetup>> =
        configurationStore.state.transform { state ->
            when (state) {
                is ConfigurationNotSetup -> emit(Fail(HomeViewState.NotSetup))
                is Setup -> emit(Content(state.configuration))
            }
        }

    private val saveConfigurationFlow =
        MutableStateFlow<AsyncState<Configuration, SaveConfigurationError>>(Uninitialized)

    @NativeCoroutinesState
    val state = combine(
        setupStateFlow, saveConfigurationFlow
    ) { setupState, saveConfigurationState ->
        HomeViewState(setupState, saveConfigurationState)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeViewState())


    fun setConfiguration(url: String, apiKey: String) {
        viewModelScope.launch {
            saveConfigurationFlow.emit(Loading())
            ValidateInput(bookmarkService).invoke(url.trimEnd('/'), apiKey).map { configuration ->
                withContext(Dispatchers.Default) {
                    configurationStore.set(configuration)
                }
                saveConfigurationFlow.emit(Content(configuration))
            }.mapLeft { error ->
                saveConfigurationFlow.emit(Fail(error))
            }
        }
    }


    class ValidateInput(private val bookmarkService: BookmarkService) {
        private suspend fun Configuration.testConnection(bookmarkService: BookmarkService): Validated<SaveConfigurationError.CannotConnect, Configuration> {
            return bookmarkService.testConnection(this).fold(ifLeft = {
                SaveConfigurationError.CannotConnect.invalid()
            }, ifRight = {
                valid()
            })
        }

        private suspend fun Configuration.validate(): Either<SaveConfigurationError, Configuration> =
            either {
                ensure(url.isNotEmpty()) { SaveConfigurationError.UrlEmpty }
                ensure(apiKey.isNotEmpty()) { SaveConfigurationError.ApiKeyEmpty }
                testConnection(bookmarkService).bind()
                Configuration(url, apiKey)
            }

        suspend operator fun invoke(
            url: String, apiKey: String
        ): Either<SaveConfigurationError, Configuration> {
            return Configuration(url, apiKey).validate()
        }
    }
}

data class HomeViewState(
    val configuration: AsyncState<Configuration, NotSetup> = Uninitialized,
    val saveConfigurationState: AsyncState<Configuration, SaveConfigurationError> = Uninitialized
) {

    object NotSetup

    companion object Defaults {
        val Initial = HomeViewState()
    }
}

sealed class SaveConfigurationError(val message: String) {
    object UrlEmpty : SaveConfigurationError("URL cannot be empty")
    object ApiKeyEmpty : SaveConfigurationError("Api Key cannot be empty")
    object CannotConnect : SaveConfigurationError("Cannot connect.")
}



