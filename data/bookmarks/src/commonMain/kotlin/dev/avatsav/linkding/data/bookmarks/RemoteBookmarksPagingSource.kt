package dev.avatsav.linkding.data.bookmarks

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.mapEither
import dev.avatsav.linkding.AppCoroutineDispatchers
import dev.avatsav.linkding.Logger
import dev.avatsav.linkding.api.LinkdingApiProvider
import dev.avatsav.linkding.data.bookmarks.mappers.BookmarkErrorMapper
import dev.avatsav.linkding.data.bookmarks.mappers.BookmarkMapper
import dev.avatsav.linkding.data.bookmarks.mappers.toLinkding
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.data.model.BookmarkCategory
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import kotlinx.coroutines.withContext

typealias RemoteBookmarksPagingSourceFactory = (String, BookmarkCategory, List<String>) -> RemoteBookmarksPagingSource

@Inject
class RemoteBookmarksPagingSource(
    @Assisted private val query: String,
    @Assisted private val category: BookmarkCategory,
    @Assisted private val tags: List<String>,
    private val apiProvider: LinkdingApiProvider,
    private val mapper: BookmarkMapper,
    private val errorMapper: BookmarkErrorMapper,
    private val dispatchers: AppCoroutineDispatchers,
    private val logger: Logger,
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

            apiProvider.bookmarksApi.getBookmarks(
                offset = offset,
                limit = params.loadSize,
                query = query,
                category = category.toLinkding(),
                tags = tags,
            ).mapEither(
                success = mapper::map,
                failure = errorMapper::map,
            ).fold(
                success = { result ->
                    val prevKey = if (position == 1) null else (position - 1)
                    val nextKey = if (result.nextPage != null) position + 1 else null
                    LoadResult.Page(
                        data = result.bookmarks,
                        prevKey = prevKey,
                        nextKey = nextKey,
                    )
                },
                failure = { error ->
                    LoadResult.Error(Exception(error.message))
                },
            )
        }
}
