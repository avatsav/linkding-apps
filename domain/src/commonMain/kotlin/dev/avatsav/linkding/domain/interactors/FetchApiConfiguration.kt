package dev.avatsav.linkding.domain.interactors

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.mapError
import dev.avatsav.linkding.data.config.ApiConfigRepository
import dev.avatsav.linkding.data.model.ApiConfig
import dev.avatsav.linkding.domain.Interactor
import me.tatarka.inject.annotations.Inject

@Inject
class FetchApiConfiguration(
    private val repository: ApiConfigRepository,
) : Interactor<Unit, ApiConfig?, String>() {

    override suspend fun doWork(param: Unit): Result<ApiConfig?, String> {
        return runSuspendCatching { repository.apiConfig }.mapError {
            it.message ?: "Error fetching api configuration"
        }
    }
}
