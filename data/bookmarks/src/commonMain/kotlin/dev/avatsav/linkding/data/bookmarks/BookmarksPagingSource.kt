package dev.avatsav.linkding.data.bookmarks

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import dev.avatsav.linkding.AppCoroutineDispatchers
import dev.avatsav.linkding.data.model.Bookmark
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
class BookmarksPagingSourceFactory(
    private val repository: BookmarksRepository,
    private val dispatchers: AppCoroutineDispatchers,
) {
    fun create(): BookmarksPagingSource {
        return BookmarksPagingSource(repository, dispatchers)
    }
}

class BookmarksPagingSource(
    private val repository: BookmarksRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : PagingSource<Int, Bookmark>() {

    companion object {
        private const val STARTING_PAGE_INDEX = 1
        private const val PAGE_SIZE = 20
    }

    override fun getRefreshKey(state: PagingState<Int, Bookmark>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Bookmark> {
        return withContext(dispatchers.io) {
            val position = params.key ?: STARTING_PAGE_INDEX
            val offset = if (position == 1) 0 else PAGE_SIZE * (position - 1) + 1

            when (val result = repository.getBookmarks(offset, PAGE_SIZE)) {
                is Err -> LoadResult.Error(Exception(result.error.message)) // TODO: So much for typing my errors. Find a better way.
                is Ok -> {
                    val prevKey = if (position == 1) null else (position - 1)
                    val nextKey = if (result.value.nextPage != null) position + 1 else null
                    LoadResult.Page(
                        data = result.value.bookmarks,
                        prevKey = prevKey,
                        nextKey = nextKey,
                    )
                }
            }
        }
    }
}
