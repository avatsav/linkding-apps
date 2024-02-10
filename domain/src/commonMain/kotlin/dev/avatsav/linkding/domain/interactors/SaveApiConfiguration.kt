package dev.avatsav.linkding.domain.interactors

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.mapError
import dev.avatsav.linkding.data.config.ApiConfigRepository
import dev.avatsav.linkding.data.model.ApiConfig
import dev.avatsav.linkding.domain.Interactor
import me.tatarka.inject.annotations.Inject

@Inject
class SaveApiConfiguration(
    private val repository: ApiConfigRepository,
) : Interactor<ApiConfig, Unit, String>() {

    override suspend fun doWork(param: ApiConfig): Result<Unit, String> {
        return runSuspendCatching { repository.apiConfig = param }.mapError {
            it.message ?: "Error saving api configuration"
        }
    }
}
