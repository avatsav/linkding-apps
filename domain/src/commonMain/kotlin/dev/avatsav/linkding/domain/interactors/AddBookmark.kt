package dev.avatsav.linkding.domain.interactors

import com.github.michaelbull.result.Result
import dev.avatsav.linkding.AppCoroutineDispatchers
import dev.avatsav.linkding.data.bookmarks.BookmarksRepository
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.data.model.BookmarkError
import dev.avatsav.linkding.data.model.SaveBookmark
import dev.avatsav.linkding.domain.Interactor
import kotlinx.coroutines.withContext
import dev.zacsweers.metro.Inject

@Inject
class AddBookmark(
  private val repository: BookmarksRepository,
  private val dispatchers: AppCoroutineDispatchers,
) : Interactor<SaveBookmark, Bookmark, BookmarkError>() {
  override suspend fun doWork(param: SaveBookmark): Result<Bookmark, BookmarkError> =
    withContext(dispatchers.io) { repository.saveBookmark(param) }
}
