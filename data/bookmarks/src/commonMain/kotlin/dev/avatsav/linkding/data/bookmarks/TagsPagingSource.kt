package dev.avatsav.linkding.data.bookmarks

import androidx.paging.PagingSource
import app.cash.paging.PagingState
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.mapEither
import dev.avatsav.linkding.AppCoroutineDispatchers
import dev.avatsav.linkding.Logger
import dev.avatsav.linkding.api.LinkdingApiProvider
import dev.avatsav.linkding.data.bookmarks.mappers.BookmarkErrorMapper
import dev.avatsav.linkding.data.bookmarks.mappers.TagMapper
import dev.avatsav.linkding.data.model.Tag
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

typealias TagsPagingSourceFactory = (List<Tag>) -> TagsPagingSource

@Inject
class TagsPagingSource(
    @Assisted private val selectedTags: List<Tag>,
    private val apiProvider: LinkdingApiProvider,
    private val mapper: TagMapper,
    private val errorMapper: BookmarkErrorMapper,
    private val dispatchers: AppCoroutineDispatchers,
    private val logger: Logger,
) : PagingSource<Int, Tag>() {

    companion object {
        private const val FIRST_PAGE = 1
    }

    override fun getRefreshKey(state: PagingState<Int, Tag>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Tag> =
        withContext(dispatchers.io) {
            val position = params.key ?: FIRST_PAGE
            val offset = if (position == FIRST_PAGE) 0 else params.loadSize * (position - 1) + 1

            apiProvider.tagsApi.getTags(offset, params.loadSize)
                .mapEither(
                    success = mapper::map,
                    failure = errorMapper::map,
                ).fold(
                    success = { result ->
                        val prevKey = if (position == 1) null else (position - 1)
                        val nextKey = if (result.nextPage != null) position + 1 else null
                        LoadResult.Page(
                            data = result.tags,
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
