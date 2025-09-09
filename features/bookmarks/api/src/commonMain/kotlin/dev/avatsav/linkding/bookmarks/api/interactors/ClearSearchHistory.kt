package dev.avatsav.linkding.bookmarks.api.interactors

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import dev.avatsav.linkding.bookmarks.api.SearchHistoryRepository
import dev.avatsav.linkding.domain.Interactor
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Interactor for clearing all search history. */
@Inject
class ClearSearchHistory(private val repository: SearchHistoryRepository) :
  Interactor<ClearSearchHistory.Params, Unit, Nothing>() {

  /** Clears all saved search history. */
  override suspend fun doWork(params: Params): Result<Unit, Nothing> =
    withContext(Dispatchers.Default) {
      repository.clearSearchHistory()
      Ok(Unit)
    }

  /** Parameters for ClearSearchHistory (unit params for now). */
  object Params
}
