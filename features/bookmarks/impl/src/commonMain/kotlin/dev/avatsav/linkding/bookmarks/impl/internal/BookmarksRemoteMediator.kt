package dev.avatsav.linkding.bookmarks.impl.internal

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.mapEither
import dev.avatsav.linkding.AppCoroutineDispatchers
import dev.avatsav.linkding.api.LinkdingBookmarksApi
import dev.avatsav.linkding.bookmarks.impl.mappers.BookmarkErrorMapper
import dev.avatsav.linkding.bookmarks.impl.mappers.BookmarkMapper
import dev.avatsav.linkding.bookmarks.impl.mappers.toLinkding
import dev.avatsav.linkding.data.db.daos.PagingBookmarksDao
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.data.model.BookmarkCategory
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.withContext

@OptIn(ExperimentalPagingApi::class)
@AssistedInject
class BookmarksRemoteMediator(
  @Assisted private val query: String,
  @Assisted private val category: BookmarkCategory,
  @Assisted private val tags: List<String>,
  private val bookmarksApi: LinkdingBookmarksApi,
  private val bookmarksDao: PagingBookmarksDao,
  private val dispatchers: AppCoroutineDispatchers,
  private val bookmarkMapper: BookmarkMapper,
  private val errorMapper: BookmarkErrorMapper,
) : RemoteMediator<Int, Bookmark>() {

  override suspend fun load(loadType: LoadType, state: PagingState<Int, Bookmark>): MediatorResult =
    withContext(dispatchers.io) {
      val offset =
        when (loadType) {
          REFRESH -> 0
          PREPEND -> return@withContext MediatorResult.Success(endOfPaginationReached = true)

          APPEND -> {
            bookmarksDao.countBookmarks().toInt()
          }
        }

      bookmarksApi
        .getBookmarks(
          query = query,
          tags = tags,
          category = category.toLinkding(),
          offset = offset,
          limit = state.config.pageSize,
        )
        .mapEither(success = bookmarkMapper::map, failure = errorMapper::map)
        .fold(
          success = {
            val bookmarks = it.bookmarks
            if (loadType == LoadType.REFRESH) {
              bookmarksDao.refresh(bookmarks)
            } else {
              bookmarksDao.append(bookmarks)
            }
            MediatorResult.Success(endOfPaginationReached = it.nextPage == null)
          },
          failure = { MediatorResult.Error(Exception(it.message)) },
        )
    }

  @AssistedFactory
  interface Factory {
    fun create(
      query: String,
      category: BookmarkCategory,
      tags: List<String>,
    ): BookmarksRemoteMediator
  }
}
