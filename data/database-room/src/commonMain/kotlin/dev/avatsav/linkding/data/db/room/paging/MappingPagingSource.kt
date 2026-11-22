package dev.avatsav.linkding.data.db.room.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState

/** Maps a Room offset-keyed source without changing its anchor-based refresh window. */
class MappingPagingSource<Value : Any, Mapped : Any>(
  private val source: PagingSource<Int, Value>,
  private val mapper: (Value) -> Mapped,
) : PagingSource<Int, Mapped>() {
  private val sourceInvalidatedCallback: () -> Unit = { invalidate() }
  private val wrapperInvalidatedCallback: () -> Unit = {
    source.unregisterInvalidatedCallback(sourceInvalidatedCallback)
  }

  init {
    source.registerInvalidatedCallback(sourceInvalidatedCallback)
    registerInvalidatedCallback(wrapperInvalidatedCallback)
  }

  override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Mapped> =
    when (val result = source.load(params)) {
      is LoadResult.Error -> LoadResult.Error(result.throwable)
      is LoadResult.Invalid -> LoadResult.Invalid()
      is LoadResult.Page ->
        LoadResult.Page(
          data = result.data.map(mapper),
          prevKey = result.prevKey,
          nextKey = result.nextKey,
          itemsBefore = result.itemsBefore,
          itemsAfter = result.itemsAfter,
        )
    }

  override fun getRefreshKey(state: PagingState<Int, Mapped>): Int? {
    // Match Room's offset policy: center the refresh window on the visible anchor.
    // A page's next/previous key is a load boundary, not the user's refresh position.
    return state.anchorPosition?.let { anchor ->
      (anchor - state.config.initialLoadSize / 2).coerceAtLeast(0)
    }
  }

  override val jumpingSupported: Boolean = source.jumpingSupported

  override val keyReuseSupported: Boolean = source.keyReuseSupported
}
