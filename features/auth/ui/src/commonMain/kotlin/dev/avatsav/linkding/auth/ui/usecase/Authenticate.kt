package dev.avatsav.linkding.auth.ui.usecase

import com.github.michaelbull.result.Result
import dev.avatsav.linkding.AppCoroutineDispatchers
import dev.avatsav.linkding.auth.api.AuthRepository
import dev.avatsav.linkding.data.model.AuthError
import dev.avatsav.linkding.domain.Interactor
import kotlinx.coroutines.withContext
import dev.zacsweers.metro.Inject

@Inject
class Authenticate(
  private val authRepository: AuthRepository,
  private val dispatchers: AppCoroutineDispatchers,
) : Interactor<Authenticate.Param, Unit, AuthError>() {

  override suspend fun doWork(param: Param): Result<Unit, AuthError> =
    withContext(dispatchers.io) { authRepository.authenticate(param.hostUrl, param.apiKey) }

  data class Param(val hostUrl: String, val apiKey: String)
}
