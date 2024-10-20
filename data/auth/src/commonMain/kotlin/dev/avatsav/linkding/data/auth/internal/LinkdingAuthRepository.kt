package dev.avatsav.linkding.data.auth.internal

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapError
import dev.avatsav.linkding.api.LinkdingAuthentication
import dev.avatsav.linkding.data.auth.AuthRepository
import dev.avatsav.linkding.data.auth.internal.mappers.AuthErrorMapper
import dev.avatsav.linkding.data.model.AuthError
import dev.avatsav.linkding.inject.AppScope
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@ContributesBinding(AppScope::class)
@SingleIn(AppScope::class)
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
