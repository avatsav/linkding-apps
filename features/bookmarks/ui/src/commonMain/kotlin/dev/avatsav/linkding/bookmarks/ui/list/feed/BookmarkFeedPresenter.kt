package dev.avatsav.linkding.bookmarks.ui.list.feed

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.filter
import dev.avatsav.linkding.bookmarks.api.interactors.ArchiveBookmark
import dev.avatsav.linkding.bookmarks.api.interactors.DeleteBookmark
import dev.avatsav.linkding.bookmarks.api.interactors.UnarchiveBookmark
import dev.avatsav.linkding.bookmarks.api.observers.ObserveBookmarks
import dev.avatsav.linkding.bookmarks.ui.list.BookmarkFeedUiEvent
import dev.avatsav.linkding.bookmarks.ui.list.BookmarkFeedUiEvent.Delete
import dev.avatsav.linkding.bookmarks.ui.list.BookmarkFeedUiEvent.Open
import dev.avatsav.linkding.bookmarks.ui.list.BookmarkFeedUiEvent.Refresh
import dev.avatsav.linkding.bookmarks.ui.list.BookmarkFeedUiEvent.ToggleArchive
import dev.avatsav.linkding.bookmarks.ui.list.common.PendingAction
import dev.avatsav.linkding.bookmarks.ui.list.common.rememberPendingActionHandler
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.data.model.BookmarkCategory
import dev.avatsav.linkding.viewmodel.MoleculePresenter
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

@AssistedInject
class BookmarkFeedPresenter(
  @Assisted scope: CoroutineScope,
  private val observeBookmarks: ObserveBookmarks,
  private val deleteBookmark: DeleteBookmark,
  private val archiveBookmark: ArchiveBookmark,
  private val unarchiveBookmark: UnarchiveBookmark,
  private val navigator: BookmarkFeedNavigator,
) : MoleculePresenter<BookmarkFeedUiEvent, BookmarkFeedUiState>(scope) {

  @Composable
  override fun models(events: Flow<BookmarkFeedUiEvent>): BookmarkFeedUiState {
    val actionHandler =
      rememberPendingActionHandler(
        scope = presenterScope,
        deleteBookmark = deleteBookmark,
        archiveBookmark = archiveBookmark,
        unarchiveBookmark = unarchiveBookmark,
        removePendingIdsOnSuccess = true, // Feed is backed by database
      )

    var basePagingFlow by remember { mutableStateOf<Flow<PagingData<Bookmark>>>(emptyFlow()) }

    LaunchedEffect(Unit) {
      observeBookmarks(
        ObserveBookmarks.Param(
          cached = true,
          query = "",
          category = BookmarkCategory.All,
          tags = emptyList(),
          pagingConfig = PagingConfig(initialLoadSize = 20, pageSize = 20),
        )
      )
      basePagingFlow = observeBookmarks.flow.cachedIn(presenterScope)
    }

    // Combine paging data with pendingIds changes to filter locally without reloading
    var bookmarksFlow by remember { mutableStateOf<Flow<PagingData<Bookmark>>>(emptyFlow()) }

    LaunchedEffect(basePagingFlow) {
      bookmarksFlow =
        combine(basePagingFlow, snapshotFlow { actionHandler.pendingIds.toSet() }) {
          pagingData,
          pending ->
          pagingData.filter { bookmark -> bookmark.id !in pending }
        }
    }

    val bookmarks = bookmarksFlow.collectAsLazyPagingItems()

    ObserveEvents { event ->
      when (event) {
        Refresh -> presenterScope.launch { bookmarks.refresh() }

        is ToggleArchive -> {
          val action =
            if (event.bookmark.archived) {
              PendingAction.Unarchive(bookmarkId = event.bookmark.id, bookmark = event.bookmark)
            } else {
              PendingAction.Archive(bookmarkId = event.bookmark.id, bookmark = event.bookmark)
            }
          actionHandler.scheduleAction(action)
        }

        is Delete -> {
          actionHandler.scheduleAction(
            PendingAction.Delete(bookmarkId = event.bookmark.id, bookmark = event.bookmark)
          )
        }

        is Open -> navigator.openUrl(event.bookmark.url)

        is BookmarkFeedUiEvent.Edit -> {
          // Editing bookmarks not yet implemented
        }

        BookmarkFeedUiEvent.UndoAction -> {
          actionHandler.undoAction()
        }

        BookmarkFeedUiEvent.DismissSnackbar -> {
          actionHandler.dismissSnackbar()
        }
      }
    }

    return BookmarkFeedUiState(
      bookmarks = bookmarks,
      snackbarMessage = actionHandler.snackbarMessage,
    )
  }

  @AssistedFactory
  interface Factory {
    fun create(scope: CoroutineScope): BookmarkFeedPresenter
  }
}

interface BookmarkFeedNavigator {
  fun openUrl(url: String)
}
