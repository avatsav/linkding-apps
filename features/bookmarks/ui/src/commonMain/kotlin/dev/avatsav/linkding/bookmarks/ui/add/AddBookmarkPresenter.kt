package dev.avatsav.linkding.bookmarks.ui.add

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import co.touchlab.kermit.Logger
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.avatsav.linkding.bookmarks.api.interactors.AddBookmark
import dev.avatsav.linkding.bookmarks.api.interactors.CheckBookmarkUrl
import dev.avatsav.linkding.data.model.CheckUrlResult
import dev.avatsav.linkding.data.model.SaveBookmark
import dev.avatsav.linkding.di.scope.UserScope
import dev.avatsav.linkding.ui.AddBookmarkScreen
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.launch

@AssistedInject
class AddBookmarkPresenter(
  @Assisted private val screen: AddBookmarkScreen,
  @Assisted private val navigator: Navigator,
  private val addBookmark: AddBookmark,
  private val checkBookmarkUrl: CheckBookmarkUrl,
) : Presenter<AddBookmarkUiState> {

  @CircuitInject(AddBookmarkScreen::class, UserScope::class)
  @AssistedFactory
  interface Factory {
    fun create(screen: AddBookmarkScreen, navigator: Navigator): AddBookmarkPresenter
  }

  @Composable
  override fun present(): AddBookmarkUiState {
    val scope = rememberCoroutineScope()

    var checkUrlResult: CheckUrlResult? by remember { mutableStateOf(null) }
    var errorMessage by remember { mutableStateOf("") }

    val checkingUrl by checkBookmarkUrl.inProgress.collectAsState(false)
    val saving by addBookmark.inProgress.collectAsState(false)

    return AddBookmarkUiState(
      sharedUrl = screen.sharedUrl,
      checkingUrl = checkingUrl,
      checkUrlResult = checkUrlResult,
      saving = saving,
      errorMessage = errorMessage,
    ) { event ->
      when (event) {
        AddBookmarkUiEvent.Close -> navigator.pop()
        is AddBookmarkUiEvent.Save ->
          scope.launch {
            addBookmark(SaveBookmark(event.url, event.title, event.description, event.tags.toSet()))
              .onSuccess { navigator.pop() }
              .onFailure { errorMessage = it.message }
          }

        is AddBookmarkUiEvent.CheckUrl ->
          scope.launch {
            checkBookmarkUrl(event.url)
              .onSuccess { checkUrlResult = it }
              .onFailure { Logger.e { "CheckError: $it" } }
          }
      }
    }
  }
}
