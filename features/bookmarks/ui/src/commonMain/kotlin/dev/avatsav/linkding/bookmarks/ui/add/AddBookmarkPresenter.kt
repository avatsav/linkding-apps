package dev.avatsav.linkding.bookmarks.ui.add

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import co.touchlab.kermit.Logger
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.map
import com.github.michaelbull.result.onErr
import com.github.michaelbull.result.onOk
import dev.avatsav.linkding.bookmarks.api.PendingBookmarks
import dev.avatsav.linkding.bookmarks.api.interactors.AddBookmark
import dev.avatsav.linkding.bookmarks.api.interactors.CheckBookmarkUrl
import dev.avatsav.linkding.bookmarks.api.interactors.GetBookmark
import dev.avatsav.linkding.bookmarks.api.interactors.UpdateBookmark
import dev.avatsav.linkding.bookmarks.ui.add.AddBookmarkUiEvent.CheckUrl
import dev.avatsav.linkding.bookmarks.ui.add.AddBookmarkUiEvent.Close
import dev.avatsav.linkding.bookmarks.ui.add.AddBookmarkUiEvent.Save
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.data.model.BookmarkError
import dev.avatsav.linkding.data.model.CheckUrlResult
import dev.avatsav.linkding.data.model.PendingBookmark
import dev.avatsav.linkding.data.model.SaveBookmark
import dev.avatsav.linkding.data.model.UpdateBookmark as UpdateBookmarkModel
import dev.avatsav.linkding.navigation.Route
import dev.avatsav.linkding.presenter.MoleculePresenter
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@AssistedInject
class AddBookmarkPresenter(
    @Assisted route: Route.AddBookmark,
    private val addBookmark: AddBookmark,
    private val getBookmark: GetBookmark,
    private val updateBookmark: UpdateBookmark,
    private val checkBookmarkUrl: CheckBookmarkUrl,
    private val pendingBookmarks: PendingBookmarks,
) : MoleculePresenter<AddBookmarkUiEvent, AddBookmarkUiState, AddBookmarkUiEffect>() {

  private val mode: BookmarkMode = route.toBookmarkMode()

  @Composable
  override fun models(events: Flow<AddBookmarkUiEvent>): AddBookmarkUiState {
    var existingBookmark: Bookmark? by remember { mutableStateOf(null) }
    var loadingBookmark by remember { mutableStateOf(mode is BookmarkMode.Pending) }
    var pendingBookmark: PendingBookmark? by remember { mutableStateOf(null) }
    var pendingUnavailable by remember { mutableStateOf(false) }
    var saving by remember { mutableStateOf(false) }
    var checkUrlResult: CheckUrlResult? by remember { mutableStateOf(null) }
    var errorMessage by remember { mutableStateOf("") }

    val checkingUrl by checkBookmarkUrl.inProgress.collectAsState(false)
    val effectiveBookmarkId = (mode as? BookmarkMode.Edit)?.bookmarkId ?: existingBookmark?.id

    // Load initial data based on mode
    LaunchedEffect(mode) {
      when (mode) {
        is New -> {
          /* Nothing to load */
        }
        is Shared -> {
          checkBookmarkUrl(mode.url)
              .onOk { result ->
                checkUrlResult = result
                if (result.existingBookmark != null) {
                  existingBookmark = result.existingBookmark
                  emitEffect(AddBookmarkUiEffect.ExistingBookmarkFound)
                }
              }
              .onErr { Logger.e { "CheckError: $it" } }
        }
        is BookmarkMode.Pending -> {
          pendingBookmark = pendingBookmarks.get(mode.pendingId)
          pendingUnavailable = pendingBookmark == null
          loadingBookmark = false
        }
        is Edit -> {
          loadingBookmark = true
          getBookmark(mode.bookmarkId)
              .onOk { existingBookmark = it }
              .onErr {
                Logger.e { "Failed to load bookmark: $it" }
                errorMessage = it.message
              }
          loadingBookmark = false
        }
      }
    }

    ObserveEvents { event ->
      when (event) {
        Close -> emitEffect(AddBookmarkUiEffect.NavigateUp)
        is Save ->
            if (!saving) {
              saving = true
              presenterScope.launch {
                try {
                  save(event, effectiveBookmarkId, pendingBookmark)
                      .onOk { saved ->
                        if (saved) emitEffect(AddBookmarkUiEffect.BookmarkSaved)
                        else pendingUnavailable = true
                      }
                      .onErr { errorMessage = it.message }
                } finally {
                  saving = false
                }
              }
            }

        is CheckUrl ->
            presenterScope.launch {
              if (mode is BookmarkMode.Pending) return@launch
              checkBookmarkUrl(event.url)
                  .onOk { result ->
                    checkUrlResult = result
                    // If URL already exists, switch to edit mode automatically
                    if (result.existingBookmark != null && existingBookmark == null) {
                      existingBookmark = result.existingBookmark
                      emitEffect(AddBookmarkUiEffect.ExistingBookmarkFound)
                    }
                  }
                  .onErr { Logger.e { "CheckError: $it" } }
            }
      }
    }

    return AddBookmarkUiState(
        mode = mode,
        existingBookmark = existingBookmark,
        pendingBookmark = pendingBookmark,
        pendingUnavailable = pendingUnavailable,
        loading = loadingBookmark,
        checkingUrl = checkingUrl,
        checkUrlResult = checkUrlResult,
        saving = saving,
        errorMessage = errorMessage.ifBlank { null },
    )
  }

  private suspend fun save(
      event: Save,
      serverId: Long?,
      pending: PendingBookmark?,
  ): Result<Boolean, BookmarkError> {
    val fields =
        (pending?.bookmark ?: SaveBookmark(event.url)).copy(
            url = event.url,
            title = event.title,
            description = event.description,
            notes = event.notes,
            tags = event.tags.toSet(),
        )
    return when {
      mode is BookmarkMode.Pending -> Ok(pendingBookmarks.edit(mode.pendingId, fields))
      serverId != null ->
          updateBookmark(
                  UpdateBookmarkModel(
                      id = serverId,
                      title = event.title,
                      description = event.description,
                      notes = event.notes,
                      tags = event.tags.toSet(),
                  )
              )
              .map { true }
      else -> addBookmark(fields).map { true }
    }
  }

  @AssistedFactory
  interface Factory {
    fun create(route: Route.AddBookmark): AddBookmarkPresenter
  }

  private fun Route.AddBookmark.toBookmarkMode(): BookmarkMode =
      when (this) {
        is New -> BookmarkMode.New
        is Shared -> BookmarkMode.Shared(url)
        is Edit -> BookmarkMode.Edit(bookmarkId)
        is Pending -> BookmarkMode.Pending(pendingId)
      }
}
