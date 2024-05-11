package dev.avatsav.linkding.ui.tags

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import app.cash.paging.PagingConfig
import app.cash.paging.compose.collectAsLazyPagingItems
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.internal.rememberStableCoroutineScope
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import dev.avatsav.linkding.Logger
import dev.avatsav.linkding.domain.observers.ObserveTags
import dev.avatsav.linkding.ui.TagsScreen
import dev.avatsav.linkding.ui.TagsScreenResult
import dev.avatsav.linkding.ui.compose.rememberCachedPagingFlow
import dev.avatsav.linkding.ui.tags.TagsUiEvent.Close
import dev.avatsav.linkding.ui.tags.TagsUiEvent.SelectTag
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class TagsUiPresenterFactory(
    private val presenterFactory: (Navigator, TagsScreen) -> TagsPresenter,
) : Presenter.Factory {
    override fun create(
        screen: Screen,
        navigator: Navigator,
        context: CircuitContext,
    ): Presenter<*>? {
        return when (screen) {
            is TagsScreen -> presenterFactory(navigator, screen)
            else -> null
        }
    }
}

@Inject
class TagsPresenter(
    @Assisted private val navigator: Navigator,
    @Assisted private val screen: TagsScreen,
    private val observeTags: ObserveTags,
    private val logger: Logger,
) : Presenter<TagsUiState> {

    @Composable
    override fun present(): TagsUiState {
        val selectedTags = screen.selectedTags.map { it.mapToTag() }
        val coroutineScope = rememberStableCoroutineScope()
        val tags = observeTags.flow.rememberCachedPagingFlow(coroutineScope)
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
