package dev.avatsav.linkding.data.db.room.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState

class MappingPagingSource<Key : Any, Value : Any, Mapped : Any>(
  private val source: PagingSource<Key, Value>,
  private val mapper: (Value) -> Mapped,
) : PagingSource<Key, Mapped>() {
  private val sourceInvalidatedCallback: () -> Unit = { invalidate() }
  private val wrapperInvalidatedCallback: () -> Unit = {
    source.unregisterInvalidatedCallback(sourceInvalidatedCallback)
  }

  init {
    source.registerInvalidatedCallback(sourceInvalidatedCallback)
    registerInvalidatedCallback(wrapperInvalidatedCallback)
  }

  override suspend fun load(params: LoadParams<Key>): LoadResult<Key, Mapped> =
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

  override fun getRefreshKey(state: PagingState<Key, Mapped>): Key? {
    return state.anchorPosition?.let { anchorPosition ->
      state.closestPageToPosition(anchorPosition)?.let { page -> page.prevKey ?: page.nextKey }
    }
  }

  override val jumpingSupported: Boolean = source.jumpingSupported

  override val keyReuseSupported: Boolean = source.keyReuseSupported
}
