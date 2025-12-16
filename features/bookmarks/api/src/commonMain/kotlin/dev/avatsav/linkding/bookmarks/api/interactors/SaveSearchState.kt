package dev.avatsav.linkding.bookmarks.api.interactors

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import dev.avatsav.linkding.AppCoroutineDispatchers
import dev.avatsav.linkding.bookmarks.api.SearchHistoryRepository
import dev.avatsav.linkding.data.model.SearchHistory
import dev.avatsav.linkding.domain.Interactor
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.withContext

/**
 * Interactor for saving a search state to history. Handles ensuring only non-empty queries are
 * saved and manages duplicates.
 */
@Inject
class SaveSearchState(
  private val repository: SearchHistoryRepository,
  private val dispatchers: AppCoroutineDispatchers,
) : Interactor<SaveSearchState.Params, Unit, Nothing>() {

  /** Saves a search state if it has a non-empty query. Doesn't save empty searches. */
  override suspend fun doWork(param: Params): Result<Unit, Nothing> {
    // Only save non-empty queries
    if (param.searchHistory.query.isBlank()) return Ok(Unit)

    return withContext(dispatchers.default) {
      repository.saveSearchHistory(param.searchHistory)
      Ok(Unit)
    }
  }

  /** Parameters for SaveSearchState. */
  data class Params(val searchHistory: SearchHistory)
}
