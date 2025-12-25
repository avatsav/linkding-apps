package dev.avatsav.linkding.bookmarks.api.interactors

import com.github.michaelbull.result.Result
import dev.avatsav.linkding.AppCoroutineDispatchers
import dev.avatsav.linkding.bookmarks.api.BookmarksRepository
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.data.model.BookmarkError
import dev.avatsav.linkding.domain.Interactor
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.withContext

@Inject
class GetBookmark(
  private val repository: BookmarksRepository,
  private val dispatchers: AppCoroutineDispatchers,
) : Interactor<Long, Bookmark, BookmarkError>() {
  override suspend fun doWork(param: Long): Result<Bookmark, BookmarkError> =
    withContext(dispatchers.io) { repository.getBookmark(param) }
}
