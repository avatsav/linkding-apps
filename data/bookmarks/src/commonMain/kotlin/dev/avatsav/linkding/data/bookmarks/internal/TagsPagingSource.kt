package dev.avatsav.linkding.data.bookmarks.internal

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.mapEither
import dev.avatsav.linkding.AppCoroutineDispatchers
import dev.avatsav.linkding.api.LinkdingTagsApi
import dev.avatsav.linkding.data.bookmarks.internal.mappers.BookmarkErrorMapper
import dev.avatsav.linkding.data.bookmarks.internal.mappers.TagMapper
import dev.avatsav.linkding.data.model.Tag
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.withContext

@Inject
class TagsPagingSource(
  private val tagsApi: LinkdingTagsApi,
  private val mapper: TagMapper,
  private val errorMapper: BookmarkErrorMapper,
  private val dispatchers: AppCoroutineDispatchers,
) : PagingSource<Int, Tag>() {

  companion object {
    private const val FIRST_PAGE = 1
  }

  override fun getRefreshKey(state: PagingState<Int, Tag>): Int? =
    state.anchorPosition?.let { anchorPosition ->
      val anchorPage = state.closestPageToPosition(anchorPosition)
      anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
    }

  override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Tag> =
    withContext(dispatchers.io) {
      val position = params.key ?: FIRST_PAGE
      val offset = if (position == FIRST_PAGE) 0 else params.loadSize * (position - 1) + 1

      tagsApi
        .getTags(offset, params.loadSize)
        .mapEither(success = mapper::map, failure = errorMapper::map)
        .fold(
          success = { result ->
            val prevKey = if (position == 1) null else (position - 1)
            val nextKey = if (result.nextPage != null) position + 1 else null
            LoadResult.Page(data = result.tags, prevKey = prevKey, nextKey = nextKey)
          },
          failure = { error -> LoadResult.Error(Exception(error.message)) },
        )
    }
}
