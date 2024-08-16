package dev.avatsav.linkding.data.bookmarks

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapError
import dev.avatsav.linkding.api.LinkdingAuthentication
import dev.avatsav.linkding.data.bookmarks.mappers.ConfigurationErrorMapper
import dev.avatsav.linkding.data.model.ConfigurationError
import me.tatarka.inject.annotations.Inject

@Inject
class AuthenticationRepository(
    private val authentication: LinkdingAuthentication,
    private val errorMapper: ConfigurationErrorMapper,
) {
    suspend fun authenticate(
        hostUrl: String,
        apiKey: String,
    ): Result<Unit, ConfigurationError> =
        authentication.authenticate(hostUrl, apiKey).mapError(errorMapper::map)
}
