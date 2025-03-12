package dev.avatsav.linkding.bookmarks.ui.add

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import co.touchlab.kermit.Logger
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.avatsav.linkding.data.model.CheckUrlResult
import dev.avatsav.linkding.data.model.SaveBookmark
import dev.avatsav.linkding.domain.interactors.AddBookmark
import dev.avatsav.linkding.domain.interactors.CheckBookmarkUrl
import dev.avatsav.linkding.inject.UserScope
import dev.avatsav.linkding.ui.AddBookmarkScreen
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@CircuitInject(AddBookmarkScreen::class, UserScope::class)
class AddBookmarkPresenter
@Inject
constructor(
  @Assisted private val screen: AddBookmarkScreen,
  @Assisted private val navigator: Navigator,
  private val addBookmark: AddBookmark,
  private val checkBookmarkUrl: CheckBookmarkUrl,
) : Presenter<AddBookmarkUiState> {

  @Composable
  override fun present(): AddBookmarkUiState {
    val scope = rememberCoroutineScope()

    var checkUrlResult: CheckUrlResult? by rememberSaveable { mutableStateOf(null) }
    var errorMessage by rememberSaveable { mutableStateOf("") }

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
