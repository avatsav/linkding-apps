package dev.avatsav.linkding.domain.interactors

import com.github.michaelbull.result.Result
import dev.avatsav.linkding.AppCoroutineDispatchers
import dev.avatsav.linkding.data.bookmarks.BookmarksRepository
import dev.avatsav.linkding.data.model.BookmarkError
import dev.avatsav.linkding.domain.Interactor
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
class ArchiveBookmark(
  private val repository: BookmarksRepository,
  private val dispatchers: AppCoroutineDispatchers,
) : Interactor<Long, Unit, BookmarkError>() {
  override suspend fun doWork(param: Long): Result<Unit, BookmarkError> =
    withContext(dispatchers.io) { repository.archiveBookmark(param) }
}
