package dev.avatsav.linkding.domain.interactors

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapError
import dev.avatsav.linkding.AppCoroutineDispatchers
import dev.avatsav.linkding.data.bookmarks.BookmarksRepository
import dev.avatsav.linkding.data.model.CheckUrlResult
import dev.avatsav.linkding.domain.Interactor
import me.tatarka.inject.annotations.Inject
import kotlinx.coroutines.withContext

@Inject
class CheckBookmarkUrl(
    private val repository: BookmarksRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : Interactor<String, CheckUrlResult, String>() {

    override suspend fun doWork(param: String): Result<CheckUrlResult, String> =
        withContext(dispatchers.io) {
            repository.checkUrl(param).mapError { it.message }
        }
}
