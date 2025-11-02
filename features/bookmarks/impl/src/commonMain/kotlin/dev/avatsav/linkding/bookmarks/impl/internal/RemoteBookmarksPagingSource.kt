package dev.avatsav.linkding.bookmarks.impl.internal

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.mapEither
import dev.avatsav.linkding.AppCoroutineDispatchers
import dev.avatsav.linkding.api.LinkdingBookmarksApi
import dev.avatsav.linkding.bookmarks.impl.mappers.BookmarkErrorMapper
import dev.avatsav.linkding.bookmarks.impl.mappers.BookmarkMapper
import dev.avatsav.linkding.bookmarks.impl.mappers.toLinkding
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.data.model.BookmarkCategory
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.withContext

@AssistedInject
class RemoteBookmarksPagingSource(
  @Assisted private val query: String,
  @Assisted private val category: BookmarkCategory,
  @Assisted private val tags: List<String>,
  private val bookmarksApi: LinkdingBookmarksApi,
  private val mapper: BookmarkMapper,
  private val errorMapper: BookmarkErrorMapper,
  private val dispatchers: AppCoroutineDispatchers,
) : PagingSource<Int, Bookmark>() {

  companion object {
    private const val FIRST_PAGE = 1
  }

  override fun getRefreshKey(state: PagingState<Int, Bookmark>): Int? =
    state.anchorPosition?.let { anchorPosition ->
      val anchorPage = state.closestPageToPosition(anchorPosition)
      anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
    }

  override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Bookmark> =
    withContext(dispatchers.io) {
      val position = params.key ?: FIRST_PAGE
      val offset = if (position == FIRST_PAGE) 0 else params.loadSize * (position - 1) + 1

      bookmarksApi
        .getBookmarks(
          offset = offset,
          limit = params.loadSize,
          query = query,
          category = category.toLinkding(),
          tags = tags,
        )
        .mapEither(success = mapper::map, failure = errorMapper::map)
        .fold(
          success = { result ->
            val prevKey = if (position == 1) null else (position - 1)
            val nextKey = if (result.nextPage != null) position + 1 else null
            LoadResult.Page(data = result.bookmarks, prevKey = prevKey, nextKey = nextKey)
          },
          failure = { error -> LoadResult.Error(Exception(error.message)) },
        )
    }

  @AssistedFactory
  interface Factory {
    fun create(
      query: String,
      category: BookmarkCategory,
      tags: List<String>,
    ): RemoteBookmarksPagingSource
  }
}
