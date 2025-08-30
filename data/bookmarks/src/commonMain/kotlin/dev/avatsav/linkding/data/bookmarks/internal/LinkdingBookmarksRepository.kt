package dev.avatsav.linkding.data.bookmarks.internal

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapEither
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.onSuccess
import dev.avatsav.linkding.api.LinkdingBookmarksApi
import dev.avatsav.linkding.data.bookmarks.BookmarksRepository
import dev.avatsav.linkding.data.bookmarks.internal.mappers.BookmarkErrorMapper
import dev.avatsav.linkding.data.bookmarks.internal.mappers.BookmarkMapper
import dev.avatsav.linkding.data.bookmarks.internal.mappers.CheckUrlResultMapper
import dev.avatsav.linkding.data.db.daos.BookmarksDao
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.data.model.BookmarkCategory
import dev.avatsav.linkding.data.model.BookmarkError
import dev.avatsav.linkding.data.model.CheckUrlResult
import dev.avatsav.linkding.data.model.SaveBookmark
import dev.avatsav.linkding.inject.UserScope
import kotlinx.coroutines.flow.Flow
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.ContributesBinding

@Inject
@ContributesBinding(UserScope::class)
class LinkdingBookmarksRepository(
  private val bookmarksApi: LinkdingBookmarksApi,
  private val bookmarksDao: BookmarksDao,
  private val bookmarksPagingDataFactory: BookmarksPagingDataFactory,
  private val bookmarkMapper: BookmarkMapper,
  private val checkUrlMapper: CheckUrlResultMapper,
  private val errorMapper: BookmarkErrorMapper,
) : BookmarksRepository {

  override fun getBookmarksPaged(
    cached: Boolean,
    pagingConfig: PagingConfig,
    query: String,
    category: BookmarkCategory,
    tags: List<String>,
  ): Flow<PagingData<Bookmark>> =
    bookmarksPagingDataFactory.create(
      cached = cached,
      pagingConfig = pagingConfig,
      param = BookmarksPagingDataFactory.Param(query, category, tags),
    )

  override suspend fun checkUrl(url: String): Result<CheckUrlResult, BookmarkError> =
    bookmarksApi.checkUrl(url).mapEither(success = checkUrlMapper::map, failure = errorMapper::map)

  override suspend fun saveBookmark(saveBookmark: SaveBookmark): Result<Bookmark, BookmarkError> {
    val request = bookmarkMapper.map(saveBookmark)
    return bookmarksApi
      .saveBookmark(request)
      .mapEither(success = bookmarkMapper::map, failure = errorMapper::map)
  }

  override suspend fun archiveBookmark(id: Long): Result<Unit, BookmarkError> =
    bookmarksApi
      .archiveBookmark(id)
      .onSuccess { bookmarksDao.delete(id) }
      .mapError(errorMapper::map)

  override suspend fun unarchiveBookmark(id: Long): Result<Unit, BookmarkError> =
    bookmarksApi
      .unarchiveBookmark(id)
      .onSuccess { bookmarksDao.delete(id) }
      .mapError(errorMapper::map)

  override suspend fun deleteBookmark(id: Long): Result<Unit, BookmarkError> =
    bookmarksApi.deleteBookmark(id).onSuccess { bookmarksDao.delete(id) }.mapError(errorMapper::map)
}
