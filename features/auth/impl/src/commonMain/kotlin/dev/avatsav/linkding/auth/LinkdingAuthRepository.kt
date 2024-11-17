package dev.avatsav.linkding.auth

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.onSuccess
import dev.avatsav.linkding.api.LinkdingAuthentication
import dev.avatsav.linkding.auth.api.AuthRepository
import dev.avatsav.linkding.auth.mappers.AuthErrorMapper
import dev.avatsav.linkding.data.model.ApiConfig
import dev.avatsav.linkding.data.model.AuthError
import dev.avatsav.linkding.prefs.AppPreferences
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@ContributesBinding(AppScope::class)
@SingleIn(AppScope::class)
class LinkdingAuthRepository(
    private val authentication: LinkdingAuthentication,
    private val appPrefs: AppPreferences,
    private val errorMapper: AuthErrorMapper,
) : AuthRepository {
    override suspend fun authenticate(
        hostUrl: String,
        apiKey: String,
    ): Result<Unit, AuthError> =
        authentication.authenticate(hostUrl, apiKey)
            .onSuccess { appPrefs.setApiConfig(ApiConfig(hostUrl, apiKey)) }
            .mapError(errorMapper::map)
}
