package dev.avatsav.linkding.bookmarks.ui.add

import androidx.compose.runtime.Immutable
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.data.model.CheckUrlResult
import dev.avatsav.linkding.data.model.PendingBookmark

sealed interface BookmarkMode {
  data object New : BookmarkMode

  data class Shared(val url: String) : BookmarkMode

  data class Edit(val bookmarkId: Long) : BookmarkMode

  data class Pending(val pendingId: Long) : BookmarkMode
}

@Immutable
data class AddBookmarkUiState(
    val mode: BookmarkMode,
    val existingBookmark: Bookmark? = null,
    val pendingBookmark: PendingBookmark? = null,
    val pendingUnavailable: Boolean = false,
    val loading: Boolean = false,
    val checkingUrl: Boolean = false,
    val checkUrlResult: CheckUrlResult? = null,
    val saving: Boolean = false,
    val errorMessage: String? = null,
) {
  val isEditMode: Boolean
    get() = mode is BookmarkMode.Edit || mode is BookmarkMode.Pending || existingBookmark != null

  val initialUrl: String
    get() = (mode as? BookmarkMode.Shared)?.url.orEmpty()
}

sealed interface AddBookmarkUiEvent {
  data class Save(
      val url: String,
      val title: String?,
      val description: String?,
      val notes: String?,
      val tags: List<String>,
  ) : AddBookmarkUiEvent

  data object Close : AddBookmarkUiEvent

  data class CheckUrl(val url: String) : AddBookmarkUiEvent
}

sealed interface AddBookmarkUiEffect {
  data object BookmarkSaved : AddBookmarkUiEffect

  data object NavigateUp : AddBookmarkUiEffect

  data object ExistingBookmarkFound : AddBookmarkUiEffect
}
