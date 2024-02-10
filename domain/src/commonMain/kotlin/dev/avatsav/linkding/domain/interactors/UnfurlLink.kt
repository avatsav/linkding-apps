package dev.avatsav.linkding.domain.interactors

import com.github.michaelbull.result.Result
import dev.avatsav.linkding.AppCoroutineDispatchers
import dev.avatsav.linkding.data.model.UnfurlData
import dev.avatsav.linkding.data.unfurl.Unfurler
import dev.avatsav.linkding.domain.Interactor
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
class UnfurlLink(
    private val unfurler: Unfurler,
    private val dispatchers: AppCoroutineDispatchers,
) :
    Interactor<String, UnfurlData, String>() {
    override suspend fun doWork(param: String): Result<UnfurlData, String> {
        return withContext(dispatchers.io) {
            unfurler.unfurl(param)
        }
    }
}
