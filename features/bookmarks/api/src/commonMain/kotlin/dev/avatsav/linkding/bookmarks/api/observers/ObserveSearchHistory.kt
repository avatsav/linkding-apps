package dev.avatsav.linkding.bookmarks.api.observers

import dev.avatsav.linkding.bookmarks.api.SearchHistoryRepository
import dev.avatsav.linkding.data.model.SearchHistory
import dev.avatsav.linkding.domain.Observer
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
class ObserveSearchHistory(private val repository: SearchHistoryRepository) :
  Observer<ObserveSearchHistory.Params, List<SearchHistory>>() {

  override fun createObservable(params: Params): Flow<List<SearchHistory>> {
    return repository.getSearchHistory()
  }

  object Params
}
