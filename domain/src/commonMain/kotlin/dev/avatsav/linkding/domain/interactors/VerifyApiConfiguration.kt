package dev.avatsav.linkding.domain.interactors

import com.github.michaelbull.result.Result
import dev.avatsav.linkding.AppCoroutineDispatchers
import dev.avatsav.linkding.data.bookmarks.ApiConnectionTester
import dev.avatsav.linkding.data.model.ConfigurationError
import dev.avatsav.linkding.domain.Interactor
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
class VerifyApiConfiguration(
    private val apiConnectionTester: ApiConnectionTester,
    private val dispatchers: AppCoroutineDispatchers,
) : Interactor<VerifyApiConfiguration.Param, Unit, ConfigurationError>() {

    override suspend fun doWork(param: Param): Result<Unit, ConfigurationError> {
        return withContext(dispatchers.io) {
            apiConnectionTester.test(param.hostUrl, param.apiKey)
        }
    }

    data class Param(val hostUrl: String, val apiKey: String)
}
