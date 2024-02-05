package dev.avatsav.linkding.data.bookmarks

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapError
import dev.avatsav.linkding.api.LinkdingConnectionTester
import dev.avatsav.linkding.data.model.InvalidApiConfiguration
import me.tatarka.inject.annotations.Inject

@Inject
class ApiConnectionTester(
    private val linkdingConnectionTester: LinkdingConnectionTester,
) {
    suspend fun test(
        hostUrl: String,
        apiKey: String,
    ): Result<Unit, InvalidApiConfiguration> {
        return linkdingConnectionTester.test(hostUrl, apiKey)
            .mapError { InvalidApiConfiguration(it.detail) }
    }
}
