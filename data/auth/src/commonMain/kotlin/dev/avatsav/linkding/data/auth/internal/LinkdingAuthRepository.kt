package dev.avatsav.linkding.data.auth.internal

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapError
import dev.avatsav.linkding.api.LinkdingAuthentication
import dev.avatsav.linkding.data.auth.AuthRepository
import dev.avatsav.linkding.data.auth.internal.mappers.AuthErrorMapper
import dev.avatsav.linkding.data.model.AuthError
import me.tatarka.inject.annotations.Inject

@Inject
class LinkdingAuthRepository(
    private val authentication: LinkdingAuthentication,
    private val errorMapper: AuthErrorMapper,
) : AuthRepository {
    override suspend fun authenticate(
        hostUrl: String,
        apiKey: String,
    ): Result<Unit, AuthError> =
        authentication.authenticate(hostUrl, apiKey).mapError(errorMapper::map)
}
