package dev.avatsav.linkding.bookmarks.ui.add

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import dev.avatsav.linkding.bookmarks.api.interactors.AddBookmark
import dev.avatsav.linkding.bookmarks.api.interactors.CheckBookmarkUrl
import dev.avatsav.linkding.data.model.CheckUrlResult
import dev.avatsav.linkding.data.model.SaveBookmark
import dev.avatsav.linkding.viewmodel.MoleculePresenter
import dev.avatsav.linkding.viewmodel.MoleculeViewModel
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

sealed interface AddBookmarkEffect {
  data object BookmarkSaved : AddBookmarkEffect
  data object NavigateUp : AddBookmarkEffect
}

@Inject
class AddBookmarkViewModel(addBookmarkPresenterFactory: AddBookmarkPresenter.Factory) :
  MoleculeViewModel<AddBookmarkUiEvent, AddBookmarkUiState, AddBookmarkEffect>() {
  override val presenter by lazy { addBookmarkPresenterFactory.create(viewModelScope) }
}

@AssistedInject
class AddBookmarkPresenter(
  @Assisted coroutineScope: CoroutineScope,
  private val addBookmark: AddBookmark,
  private val checkBookmarkUrl: CheckBookmarkUrl,
  private val sharedUrl: String?,
) : MoleculePresenter<AddBookmarkUiEvent, AddBookmarkUiState, AddBookmarkEffect>(coroutineScope) {

  @Composable
  override fun models(events: Flow<AddBookmarkUiEvent>): AddBookmarkUiState {
    var checkUrlResult: CheckUrlResult? by remember { mutableStateOf(null) }
    var errorMessage by remember { mutableStateOf("") }

    val checkingUrl by checkBookmarkUrl.inProgress.collectAsState(false)
    val saving by addBookmark.inProgress.collectAsState(false)

    ObserveEvents { event ->
      when (event) {
        AddBookmarkUiEvent.Close -> trySendEffect(AddBookmarkEffect.NavigateUp)
        is AddBookmarkUiEvent.Save ->
          presenterScope.launch {
            addBookmark(SaveBookmark(event.url, event.title, event.description, event.tags.toSet()))
              .onSuccess {
                emitEffect(AddBookmarkEffect.BookmarkSaved)
                emitEffect(AddBookmarkEffect.NavigateUp)
              }
              .onFailure { errorMessage = it.message }
          }

        is AddBookmarkUiEvent.CheckUrl ->
          presenterScope.launch {
            checkBookmarkUrl(event.url)
              .onSuccess { checkUrlResult = it }
              .onFailure { Logger.e { "CheckError: $it" } }
          }
      }
    }

    return AddBookmarkUiState(
      sharedUrl = sharedUrl,
      checkingUrl = checkingUrl,
      checkUrlResult = checkUrlResult,
      saving = saving,
      errorMessage = errorMessage,
    )
  }

  @AssistedFactory
  interface Factory {
    fun create(coroutineScope: CoroutineScope): AddBookmarkPresenter
  }
}
