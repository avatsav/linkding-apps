package dev.avatsav.linkding.data.db.room

import androidx.paging.PagingSource
import androidx.paging.PagingState

class MappingPagingSource<Key : Any, Value : Any, Mapped : Any>(
  private val source: PagingSource<Key, Value>,
  private val mapper: (Value) -> Mapped,
) : PagingSource<Key, Mapped>() {
  private val invalidatedCallback: () -> Unit = { invalidate() }

  init {
    source.registerInvalidatedCallback(invalidatedCallback)
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

  @Suppress("UNCHECKED_CAST")
  override fun getRefreshKey(state: PagingState<Key, Mapped>): Key? {
    return source.getRefreshKey(state as PagingState<Key, Value>)
  }

  override val jumpingSupported: Boolean = source.jumpingSupported

  override val keyReuseSupported: Boolean = source.keyReuseSupported
}
