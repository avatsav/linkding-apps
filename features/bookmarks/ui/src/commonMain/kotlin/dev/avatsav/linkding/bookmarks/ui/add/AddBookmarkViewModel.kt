package dev.avatsav.linkding.bookmarks.ui.add

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import co.touchlab.kermit.Logger
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import dev.avatsav.linkding.bookmarks.api.interactors.AddBookmark
import dev.avatsav.linkding.bookmarks.api.interactors.CheckBookmarkUrl
import dev.avatsav.linkding.bookmarks.api.interactors.GetBookmark
import dev.avatsav.linkding.bookmarks.api.interactors.UpdateBookmark
import dev.avatsav.linkding.bookmarks.ui.add.AddBookmarkUiEvent.CheckUrl
import dev.avatsav.linkding.bookmarks.ui.add.AddBookmarkUiEvent.Close
import dev.avatsav.linkding.bookmarks.ui.add.AddBookmarkUiEvent.Save
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.data.model.CheckUrlResult
import dev.avatsav.linkding.data.model.SaveBookmark
import dev.avatsav.linkding.data.model.UpdateBookmark as UpdateBookmarkModel
import dev.avatsav.linkding.navigation.Route
import dev.avatsav.linkding.viewmodel.MoleculePresenter
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@AssistedInject
class AddBookmarkPresenter(
  @Assisted private val route: Route.AddBookmark,
  private val addBookmark: AddBookmark,
  private val getBookmark: GetBookmark,
  private val updateBookmark: UpdateBookmark,
  private val checkBookmarkUrl: CheckBookmarkUrl,
) : MoleculePresenter<AddBookmarkUiEvent, AddBookmarkUiState, AddBookmarkUiEffect>() {

  private val mode: BookmarkMode = route.toBookmarkMode()

  @Composable
  override fun models(events: Flow<AddBookmarkUiEvent>): AddBookmarkUiState {
    var existingBookmark: Bookmark? by remember { mutableStateOf(null) }
    var loadingBookmark by remember { mutableStateOf(false) }
    var checkUrlResult: CheckUrlResult? by remember { mutableStateOf(null) }
    var errorMessage by remember { mutableStateOf("") }

    val checkingUrl by checkBookmarkUrl.inProgress.collectAsState(false)
    val savingNew by addBookmark.inProgress.collectAsState(false)
    val updating by updateBookmark.inProgress.collectAsState(false)
    val saving = savingNew || updating

    val isEditMode = mode is BookmarkMode.Edit || existingBookmark != null
    val effectiveBookmarkId = (mode as? BookmarkMode.Edit)?.bookmarkId ?: existingBookmark?.id

    // Load initial data based on mode
    LaunchedEffect(mode) {
      when (mode) {
        is New -> {
          /* Nothing to load */
        }
        is Shared -> {
          checkBookmarkUrl(mode.url)
            .onSuccess { result ->
              checkUrlResult = result
              if (result.existingBookmark != null) {
                existingBookmark = result.existingBookmark
                emitEffect(AddBookmarkUiEffect.ExistingBookmarkFound)
              }
            }
            .onFailure { Logger.e { "CheckError: $it" } }
        }
        is Edit -> {
          loadingBookmark = true
          getBookmark(mode.bookmarkId)
            .onSuccess { existingBookmark = it }
            .onFailure {
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
          presenterScope.launch {
            if (isEditMode && effectiveBookmarkId != null) {
              updateBookmark(
                  UpdateBookmarkModel(
                    id = effectiveBookmarkId,
                    title = event.title,
                    description = event.description,
                    notes = event.notes,
                    tags = event.tags.toSet(),
                  )
                )
                .onSuccess { emitEffect(AddBookmarkUiEffect.BookmarkSaved) }
                .onFailure { errorMessage = it.message }
            } else {
              addBookmark(
                  SaveBookmark(
                    url = event.url,
                    title = event.title,
                    description = event.description,
                    notes = event.notes,
                    tags = event.tags.toSet(),
                  )
                )
                .onSuccess { emitEffect(AddBookmarkUiEffect.BookmarkSaved) }
                .onFailure { errorMessage = it.message }
            }
          }

        is CheckUrl ->
          presenterScope.launch {
            checkBookmarkUrl(event.url)
              .onSuccess { result ->
                checkUrlResult = result
                // If URL already exists, switch to edit mode automatically
                if (result.existingBookmark != null && existingBookmark == null) {
                  existingBookmark = result.existingBookmark
                  emitEffect(AddBookmarkUiEffect.ExistingBookmarkFound)
                }
              }
              .onFailure { Logger.e { "CheckError: $it" } }
          }
      }
    }

    return AddBookmarkUiState(
      mode = mode,
      existingBookmark = existingBookmark,
      loading = loadingBookmark,
      checkingUrl = checkingUrl,
      checkUrlResult = checkUrlResult,
      saving = saving,
      errorMessage = errorMessage.ifBlank { null },
    )
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
    }
}
