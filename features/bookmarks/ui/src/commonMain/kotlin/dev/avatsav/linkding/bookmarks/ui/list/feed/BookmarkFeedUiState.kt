package dev.avatsav.linkding.bookmarks.ui.list.feed

import androidx.paging.compose.LazyPagingItems
import dev.avatsav.linkding.bookmarks.ui.list.common.SnackbarMessage
import dev.avatsav.linkding.data.model.Bookmark

data class BookmarkFeedUiState(
  val bookmarks: LazyPagingItems<Bookmark>,
  val snackbarMessage: SnackbarMessage? = null,
)
