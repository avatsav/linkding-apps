package dev.avatsav.linkding.domain.interactors

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.mapError
import dev.avatsav.linkding.data.model.ApiConfig
import dev.avatsav.linkding.domain.Interactor
import dev.avatsav.linkding.prefs.AppPreferences
import me.tatarka.inject.annotations.Inject

@Inject
class SaveApiConfiguration(private val prefs: AppPreferences) :
    Interactor<ApiConfig, Unit, SaveApiConfiguration.Error>() {

    override suspend fun doWork(param: ApiConfig): Result<Unit, Error> =
        runSuspendCatching {
            prefs.apiConfiguration = param
        }.mapError {
            Error(it.message ?: "Error saving api configuration")
        }

    data class Error(val message: String)
}
