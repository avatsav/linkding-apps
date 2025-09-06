package dev.avatsav.linkding.bookmarks.api.interactors

import com.github.michaelbull.result.Result
import dev.avatsav.linkding.AppCoroutineDispatchers
import dev.avatsav.linkding.bookmarks.api.BookmarksRepository
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.data.model.BookmarkError
import dev.avatsav.linkding.data.model.SaveBookmark
import dev.avatsav.linkding.domain.Interactor
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.withContext

@Inject
class AddBookmark(
  private val repository: BookmarksRepository,
  private val dispatchers: AppCoroutineDispatchers,
) : Interactor<SaveBookmark, Bookmark, BookmarkError>() {
  override suspend fun doWork(param: SaveBookmark): Result<Bookmark, BookmarkError> =
    withContext(dispatchers.io) { repository.saveBookmark(param) }
}
