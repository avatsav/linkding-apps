package dev.avatsav.linkding.bookmarks.ui.tags

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.compose.collectAsLazyPagingItems
import dev.avatsav.linkding.bookmarks.api.observers.ObserveTags
import dev.avatsav.linkding.bookmarks.ui.tags.TagsUiEvent.Close
import dev.avatsav.linkding.bookmarks.ui.tags.TagsUiEvent.Confirm
import dev.avatsav.linkding.bookmarks.ui.tags.TagsUiEvent.ToggleTag
import dev.avatsav.linkding.bookmarks.ui.util.loadedList
import dev.avatsav.linkding.data.model.Tag
import dev.avatsav.linkding.presenter.MoleculePresenter
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@AssistedInject
class TagsPresenter(
  @Assisted private val initialSelectedTagIds: Set<Long>,
  private val observeTags: ObserveTags,
) : MoleculePresenter<TagsUiEvent, TagsUiState, TagsUiEffect>() {

  @Composable
  override fun models(events: Flow<TagsUiEvent>): TagsUiState {
    var selectedTagIds by remember { mutableStateOf(initialSelectedTagIds) }

    val tagsFlow: Flow<PagingData<Tag>> by
      produceState(emptyFlow()) {
        observeTags(
          ObserveTags.Param(emptyList(), PagingConfig(initialLoadSize = 100, pageSize = 100))
        )
        value = observeTags.flow.cachedIn(presenterScope)
      }
    val tags = tagsFlow.collectAsLazyPagingItems()

    ObserveEvents { event ->
      when (event) {
        is ToggleTag -> {
          selectedTagIds =
            if (event.tag.id in selectedTagIds) selectedTagIds - event.tag.id
            else selectedTagIds + event.tag.id
        }
        Confirm -> {
          val selectedTags = tags.loadedList().filter { it.id in selectedTagIds }
          emitEffect(TagsUiEffect.TagsConfirmed(selectedTags))
        }
        Close -> emitEffect(TagsUiEffect.Dismiss)
      }
    }

    return TagsUiState(selectedTagIds = selectedTagIds, tags = tags)
  }

  @AssistedFactory
  interface Factory {
    fun create(initialSelectedTagIds: Set<Long>): TagsPresenter
  }
}
