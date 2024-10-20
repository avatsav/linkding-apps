package dev.avatsav.linkding.ui.tags

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.paging.PagingConfig
import androidx.paging.compose.collectAsLazyPagingItems
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.avatsav.linkding.Logger
import dev.avatsav.linkding.domain.observers.ObserveTags
import dev.avatsav.linkding.inject.UserScope
import dev.avatsav.linkding.ui.TagsScreen
import dev.avatsav.linkding.ui.TagsScreenResult
import dev.avatsav.linkding.ui.circuit.rememberRetainedCachedPagingFlow
import dev.avatsav.linkding.ui.tags.TagsUiEvent.Close
import dev.avatsav.linkding.ui.tags.TagsUiEvent.SelectTag
import me.tatarka.inject.annotations.Assisted

@CircuitInject(TagsScreen::class, UserScope::class)
class TagsPresenter(
    @Assisted private val navigator: Navigator,
    @Assisted private val screen: TagsScreen,
    private val observeTags: ObserveTags,
    private val logger: Logger,
) : Presenter<TagsUiState> {

    @Composable
    override fun present(): TagsUiState {
        val selectedTags = screen.selectedTags.map { it.mapToTag() }

        val tags = observeTags.flow
            .rememberRetainedCachedPagingFlow()
            .collectAsLazyPagingItems()

        LaunchedEffect(Unit) {
            observeTags(
                ObserveTags.Param(
                    selectedTags,
                    PagingConfig(
                        initialLoadSize = 100,
                        pageSize = 100,
                    ),
                ),
            )
        }

        return TagsUiState(
            selectedTags = selectedTags,
            tags = tags,
        ) { event ->
            when (event) {
                is SelectTag -> navigator.pop(
                    TagsScreenResult.Selected(event.tag.mapToScreenParam()),
                )

                Close -> navigator.pop(TagsScreenResult.Dismissed)
            }
        }
    }
}
