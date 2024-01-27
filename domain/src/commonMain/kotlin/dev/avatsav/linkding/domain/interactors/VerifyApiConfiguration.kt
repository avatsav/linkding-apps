package dev.avatsav.linkding.domain.interactors

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.mapError
import dev.avatsav.linkding.AppCoroutineDispatchers
import dev.avatsav.linkding.data.bookmarks.ApiConnectionTester
import dev.avatsav.linkding.domain.Interactor
import dev.avatsav.linkding.inject.ApplicationScope
import kotlinx.coroutines.withContext

@ApplicationScope
class VerifyApiConfiguration(
    private val apiConnectionTester: ApiConnectionTester,
    private val dispatchers: AppCoroutineDispatchers,
) : Interactor<VerifyApiConfiguration.Param, Unit, VerifyApiConfiguration.Error>() {

    override suspend fun doWork(param: Param): Result<Unit, Error> {
        return withContext(dispatchers.io) {
            binding {
                param.validateApiKey().bind()
                param.validateHostUrl().bind()
            }.andThen {
                testConnection(param.hostUrl, param.apiKey)
            }
        }
    }

    private suspend fun testConnection(
        hostUrl: String,
        apiKey: String,
    ): Result<Unit, Error.CannotConnect> {
        return apiConnectionTester.test(hostUrl, apiKey).mapError { Error.CannotConnect }
    }

    private fun Param.validateHostUrl(): Result<Unit, Error.UrlEmpty> {
        return if (hostUrl.isEmpty()) Err(Error.UrlEmpty) else Ok(Unit)
    }

    private fun Param.validateApiKey(): Result<Unit, Error.ApiKeyEmpty> {
        return if (apiKey.isEmpty()) Err(Error.ApiKeyEmpty) else Ok(Unit)
    }

    data class Param(val hostUrl: String, val apiKey: String)

    sealed class Error(val message: String) {
        data object UrlEmpty : Error("URL cannot be empty")
        data object ApiKeyEmpty : Error("Api Key cannot be empty")
        data object CannotConnect : Error("Cannot connect.")
    }
}


