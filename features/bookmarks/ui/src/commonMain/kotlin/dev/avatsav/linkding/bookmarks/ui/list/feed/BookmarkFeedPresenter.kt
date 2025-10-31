package dev.avatsav.linkding.bookmarks.ui.list.feed

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.filter
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.avatsav.linkding.bookmarks.api.interactors.ArchiveBookmark
import dev.avatsav.linkding.bookmarks.api.interactors.DeleteBookmark
import dev.avatsav.linkding.bookmarks.api.interactors.UnarchiveBookmark
import dev.avatsav.linkding.bookmarks.api.observers.ObserveBookmarks
import dev.avatsav.linkding.bookmarks.ui.list.common.PendingAction
import dev.avatsav.linkding.bookmarks.ui.list.common.rememberPendingActionHandler
import dev.avatsav.linkding.bookmarks.ui.list.feed.BookmarkListUiEvent.Delete
import dev.avatsav.linkding.bookmarks.ui.list.feed.BookmarkListUiEvent.Open
import dev.avatsav.linkding.bookmarks.ui.list.feed.BookmarkListUiEvent.Refresh
import dev.avatsav.linkding.bookmarks.ui.list.feed.BookmarkListUiEvent.ToggleArchive
import dev.avatsav.linkding.data.model.BookmarkCategory
import dev.avatsav.linkding.ui.UrlScreen
import dev.avatsav.linkding.ui.circuit.rememberRetainedCoroutineScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

@AssistedInject
class BookmarkFeedPresenter(
  @Assisted private val navigator: Navigator,
  private val observeBookmarks: ObserveBookmarks,
  private val deleteBookmark: DeleteBookmark,
  private val archiveBookmark: ArchiveBookmark,
  private val unarchiveBookmark: UnarchiveBookmark,
) : Presenter<BookmarkListUiState> {

  @AssistedFactory
  interface Factory {
    fun create(navigator: Navigator): BookmarkFeedPresenter
  }

  @Composable
  override fun present(): BookmarkListUiState {
    val presenterScope = rememberRetainedCoroutineScope()

    val actionHandler =
      rememberPendingActionHandler(
        scope = presenterScope,
        deleteBookmark = deleteBookmark,
        archiveBookmark = archiveBookmark,
        unarchiveBookmark = unarchiveBookmark,
        removePendingIdsOnSuccess = true, // Feed is backed by database
      )

    val basePagingFlow by
      produceRetainedState(emptyFlow()) {
        observeBookmarks(
          ObserveBookmarks.Param(
            cached = true,
            query = "",
            category = BookmarkCategory.All,
            tags = emptyList(),
            pagingConfig = PagingConfig(initialLoadSize = 20, pageSize = 20),
          )
        )
        value = observeBookmarks.flow.cachedIn(presenterScope)
      }

    // Combine paging data with pendingIds changes to filter locally without reloading
    val bookmarksFlow by
      produceRetainedState(emptyFlow(), basePagingFlow) {
        value =
          combine(basePagingFlow, snapshotFlow { actionHandler.pendingIds.toSet() }) {
            pagingData,
            pending ->
            pagingData.filter { bookmark -> bookmark.id !in pending }
          }
      }

    val bookmarks = bookmarksFlow.collectAsLazyPagingItems()

    return BookmarkListUiState(
      bookmarks = bookmarks,
      snackbarMessage = actionHandler.snackbarMessage,
      eventSink = { event ->
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

          is Open -> navigator.goTo(UrlScreen(event.bookmark.url))

          is BookmarkListUiEvent.Edit -> {
            // Editing bookmarks not yet implemented
          }

          BookmarkListUiEvent.UndoAction -> {
            actionHandler.undoAction()
          }

          BookmarkListUiEvent.DismissSnackbar -> {
            actionHandler.dismissSnackbar()
          }
        }
      },
    )
  }
}
