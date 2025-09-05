package dev.avatsav.linkding.bookmarks.api.interactors

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapError
import dev.avatsav.linkding.AppCoroutineDispatchers
import dev.avatsav.linkding.bookmarks.api.BookmarksRepository
import dev.avatsav.linkding.data.model.CheckUrlResult
import dev.avatsav.linkding.domain.Interactor
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.withContext

@Inject
class CheckBookmarkUrl(
  private val repository: BookmarksRepository,
  private val dispatchers: AppCoroutineDispatchers,
) : Interactor<String, CheckUrlResult, String>() {

  override suspend fun doWork(param: String): Result<CheckUrlResult, String> =
    withContext(dispatchers.io) { repository.checkUrl(param).mapError { it.message } }
}
