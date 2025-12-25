package dev.avatsav.linkding.bookmarks.api.interactors

import com.github.michaelbull.result.Result
import dev.avatsav.linkding.AppCoroutineDispatchers
import dev.avatsav.linkding.bookmarks.api.BookmarksRepository
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.data.model.BookmarkError
import dev.avatsav.linkding.data.model.UpdateBookmark
import dev.avatsav.linkding.domain.Interactor
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.withContext

@Inject
class UpdateBookmark(
  private val repository: BookmarksRepository,
  private val dispatchers: AppCoroutineDispatchers,
) : Interactor<UpdateBookmark, Bookmark, BookmarkError>() {
  override suspend fun doWork(param: UpdateBookmark): Result<Bookmark, BookmarkError> =
    withContext(dispatchers.io) { repository.updateBookmark(param) }
}
