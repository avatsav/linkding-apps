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
import dev.avatsav.linkding.viewmodel.MoleculeViewModel
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Inject
class TagsViewModel(
  private val observeTags: ObserveTags,
  private val navigator: TagsNavigator,
  private val selectedTags: List<Tag>,
) : MoleculeViewModel<TagsUiEvent, TagsUiState>() {

  @Composable
  override fun models(events: Flow<TagsUiEvent>): TagsUiState {
    var tagsFlow by remember { mutableStateOf<Flow<PagingData<Tag>>>(emptyFlow()) }

    LaunchedEffect(selectedTags) {
      observeTags(
        ObserveTags.Param(selectedTags, PagingConfig(initialLoadSize = 100, pageSize = 100))
      )
      tagsFlow = observeTags.flow.cachedIn(viewModelScope)
    }

    val tags = tagsFlow.collectAsLazyPagingItems()

    CollectEvents { event ->
      when (event) {
        is SelectTag -> navigator.onTagSelected(event.tag)
        Close -> navigator.onDismiss()
      }
    }

    return TagsUiState(selectedTags = selectedTags, tags = tags)
  }
}

interface TagsNavigator {
  fun onTagSelected(tag: Tag)

  fun onDismiss()
}
