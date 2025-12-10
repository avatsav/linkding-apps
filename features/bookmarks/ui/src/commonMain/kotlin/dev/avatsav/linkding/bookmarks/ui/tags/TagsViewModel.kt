package dev.avatsav.linkding.bookmarks.ui.tags

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.compose.collectAsLazyPagingItems
import dev.avatsav.linkding.bookmarks.api.observers.ObserveTags
import dev.avatsav.linkding.bookmarks.ui.tags.TagsUiEvent.Close
import dev.avatsav.linkding.bookmarks.ui.tags.TagsUiEvent.Confirm
import dev.avatsav.linkding.bookmarks.ui.tags.TagsUiEvent.ToggleTag
import dev.avatsav.linkding.data.model.Tag
import dev.avatsav.linkding.di.scope.UserScope
import dev.avatsav.linkding.viewmodel.MoleculePresenter
import dev.avatsav.linkding.viewmodel.MoleculeViewModel
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@AssistedInject
class TagsViewModel(
  @Assisted private val initialSelectedTagIds: List<Long>,
  tagsPresenterFactory: TagsPresenter.Factory,
) : MoleculeViewModel<TagsUiEvent, TagsUiState, TagsUiEffect>() {
  override val presenter by lazy {
    tagsPresenterFactory.create(viewModelScope, initialSelectedTagIds)
  }

  @AssistedFactory
  @ManualViewModelAssistedFactoryKey(Factory::class)
  @ContributesIntoMap(UserScope::class)
  interface Factory : ManualViewModelAssistedFactory {
    fun create(initialSelectedTagIds: List<Long>): TagsViewModel
  }
}

@AssistedInject
class TagsPresenter(
  @Assisted coroutineScope: CoroutineScope,
  @Assisted private val initialSelectedTagIds: List<Long>,
  private val observeTags: ObserveTags,
) : MoleculePresenter<TagsUiEvent, TagsUiState, TagsUiEffect>(coroutineScope) {

  @Composable
  override fun models(events: Flow<TagsUiEvent>): TagsUiState {
    var selectedTags by remember { mutableStateOf<List<Tag>>(emptyList()) }

    val tagsFlow: Flow<PagingData<Tag>> by
      produceState(emptyFlow()) {
        observeTags(
          ObserveTags.Param(emptyList(), PagingConfig(initialLoadSize = 100, pageSize = 100))
        )
        value = observeTags.flow.cachedIn(presenterScope)
      }
    val tags = tagsFlow.collectAsLazyPagingItems()

    // Track the IDs we've initialized with to detect changes
    var initializedWithIds by remember { mutableStateOf<List<Long>?>(null) }

    LaunchedEffect(tags.itemCount, initialSelectedTagIds) {
      // Initialize when tags are loaded and IDs haven't been processed yet
      if (initializedWithIds != initialSelectedTagIds && tags.itemCount > 0) {
        initializedWithIds = initialSelectedTagIds
        val allTags = (0 until tags.itemCount).mapNotNull { tags[it] }
        selectedTags = allTags.filter { it.id in initialSelectedTagIds }
      }
    }

    ObserveEvents { event ->
      when (event) {
        is ToggleTag -> {
          selectedTags =
            if (event.tag in selectedTags) {
              selectedTags - event.tag
            } else {
              selectedTags + event.tag
            }
        }
        Confirm -> emitEffect(TagsUiEffect.TagsConfirmed(selectedTags))
        Close -> emitEffect(TagsUiEffect.Dismiss)
      }
    }

    return TagsUiState(selectedTags = selectedTags, tags = tags)
  }

  @AssistedFactory
  interface Factory {
    fun create(coroutineScope: CoroutineScope, initialSelectedTagIds: List<Long>): TagsPresenter
  }
}
