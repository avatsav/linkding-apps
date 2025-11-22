package dev.avatsav.linkding.bookmarks.ui.tags

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.compose.collectAsLazyPagingItems
import dev.avatsav.linkding.bookmarks.api.observers.ObserveTags
import dev.avatsav.linkding.bookmarks.ui.tags.TagsUiEvent.Close
import dev.avatsav.linkding.bookmarks.ui.tags.TagsUiEvent.SelectTag
import dev.avatsav.linkding.data.model.Tag
import dev.avatsav.linkding.viewmodel.MoleculePresenter
import dev.avatsav.linkding.viewmodel.MoleculeViewModel
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@AssistedInject
class TagsViewModel(
  @Assisted private val selectedTags: List<Tag>,
  tagsPresenterFactory: TagsPresenter.Factory,
) : MoleculeViewModel<TagsUiEvent, TagsUiState, TagsEffect>() {
  override val presenter by lazy { tagsPresenterFactory.create(viewModelScope, selectedTags) }

  @AssistedFactory
  interface Factory {
    fun create(selectedTags: List<Tag>): TagsViewModel
  }
}

@AssistedInject
class TagsPresenter(
  @Assisted coroutineScope: CoroutineScope,
  @Assisted private val selectedTags: List<Tag>,
  private val observeTags: ObserveTags,
) : MoleculePresenter<TagsUiEvent, TagsUiState, TagsEffect>(coroutineScope) {

  @Composable
  override fun models(events: Flow<TagsUiEvent>): TagsUiState {
    var tagsFlow by remember { mutableStateOf<Flow<PagingData<Tag>>>(emptyFlow()) }

    LaunchedEffect(selectedTags) {
      observeTags(
        ObserveTags.Param(selectedTags, PagingConfig(initialLoadSize = 100, pageSize = 100))
      )
      tagsFlow = observeTags.flow.cachedIn(presenterScope)
    }

    val tags = tagsFlow.collectAsLazyPagingItems()

    ObserveEvents { event ->
      when (event) {
        is SelectTag -> trySendEffect(TagsEffect.TagSelected(event.tag))
        Close -> trySendEffect(TagsEffect.Dismiss)
      }
    }

    return TagsUiState(selectedTags = selectedTags, tags = tags)
  }

  @AssistedFactory
  interface Factory {
    fun create(coroutineScope: CoroutineScope, selectedTags: List<Tag>): TagsPresenter
  }
}
