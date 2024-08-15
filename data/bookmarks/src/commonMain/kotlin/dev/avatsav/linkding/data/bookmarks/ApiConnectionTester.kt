package dev.avatsav.linkding.data.bookmarks

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapError
import dev.avatsav.linkding.api.LinkdingConnection
import dev.avatsav.linkding.data.bookmarks.mappers.ConfigurationErrorMapper
import dev.avatsav.linkding.data.model.ConfigurationError
import me.tatarka.inject.annotations.Inject

@Inject
class ApiConnectionTester(
    private val linkdingConnection: LinkdingConnection,
    private val errorMapper: ConfigurationErrorMapper,
) {
    suspend fun test(
        hostUrl: String,
        apiKey: String,
    ): Result<Unit, ConfigurationError> = linkdingConnection.connect(hostUrl, apiKey)
        .mapError(errorMapper::map)
}
