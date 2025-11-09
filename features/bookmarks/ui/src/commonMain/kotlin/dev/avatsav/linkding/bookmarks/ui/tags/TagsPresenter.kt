package dev.avatsav.linkding.bookmarks.ui.tags

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.compose.collectAsLazyPagingItems
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.avatsav.linkding.bookmarks.api.observers.ObserveTags
import dev.avatsav.linkding.bookmarks.ui.tags.TagsUiEvent.Close
import dev.avatsav.linkding.bookmarks.ui.tags.TagsUiEvent.SelectTag
import dev.avatsav.linkding.di.scope.UserScope
import dev.avatsav.linkding.ui.TagsScreen
import dev.avatsav.linkding.ui.TagsScreenResult
import dev.avatsav.linkding.ui.circuit.produceRetainedState
import dev.avatsav.linkding.ui.circuit.rememberRetainedCoroutineScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.flow.emptyFlow

@AssistedInject
class TagsPresenter(
  @Assisted private val navigator: Navigator,
  @Assisted private val screen: TagsScreen,
  private val observeTags: ObserveTags,
) : Presenter<TagsUiState> {

  @CircuitInject(TagsScreen::class, UserScope::class)
  @AssistedFactory
  interface Factory {
    fun create(navigator: Navigator, screen: TagsScreen): TagsPresenter
  }

  @Composable
  override fun present(): TagsUiState {
    val presenterScope = rememberRetainedCoroutineScope()
    val selectedTags = screen.selectedTags.map { it.mapToTag() }

    val tagsFlow by
      produceRetainedState(emptyFlow()) {
        observeTags(
          ObserveTags.Param(selectedTags, PagingConfig(initialLoadSize = 100, pageSize = 100))
        )
        value = observeTags.flow.cachedIn(presenterScope)
      }
    val tags = tagsFlow.collectAsLazyPagingItems()

    return TagsUiState(selectedTags = selectedTags, tags = tags) { event ->
      when (event) {
        is SelectTag -> navigator.pop(TagsScreenResult.Selected(event.tag.mapToScreenParam()))

        Close -> navigator.pop(TagsScreenResult.Dismissed)
      }
    }
  }
}
