package dev.avatsav.linkding.bookmarks.ui.tags

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.avatsav.linkding.navigation.LocalNavigator
import dev.avatsav.linkding.navigation.Route
import dev.avatsav.linkding.ui.compose.none
import dev.avatsav.linkding.viewmodel.ObserveEffects

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagsScreen(viewModel: TagsViewModel, modifier: Modifier = Modifier) {
  val navigator = LocalNavigator.current
  val state by viewModel.models.collectAsStateWithLifecycle()
  val eventSink = viewModel::eventSink

  ObserveEffects(viewModel.effects) { effect ->
    when (effect) {
      is TagsUiEffect.TagsConfirmed -> {
        navigator.pop(Route.Tags.Result.Confirmed(effect.selectedTags))
      }
      TagsUiEffect.Dismiss -> navigator.pop(Route.Tags.Result.Dismissed)
    }
  }

  TagsContent(state, modifier, eventSink)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TagsContent(
  state: TagsUiState,
  modifier: Modifier = Modifier,
  eventSink: (TagsUiEvent) -> Unit,
) {
  val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
  Scaffold(
    modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    topBar = {
      TopAppBar(
        windowInsets = WindowInsets.none,
        title = { Text("Select Tags") },
        navigationIcon = {
          IconButton(onClick = { eventSink(TagsUiEvent.Close) }) {
            Icon(imageVector = Icons.Filled.Close, contentDescription = "Close")
          }
        },
        colors =
          TopAppBarDefaults.topAppBarColors(
            scrolledContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(16.dp)
          ),
        scrollBehavior = scrollBehavior,
      )
    },
    bottomBar = {
      BottomAppBar(
        floatingActionButton = {
          Button(
            modifier = Modifier.defaultMinSize(minWidth = 56.dp, minHeight = 48.dp),
            onClick = { eventSink(TagsUiEvent.Confirm) },
          ) {
            Text("Confirm")
          }
        },
        actions = {
          if (state.selectedTagIds.isNotEmpty()) {
            Text(
              modifier = Modifier.padding(horizontal = 16.dp),
              text = "${state.selectedTagIds.size} selected",
              style = MaterialTheme.typography.bodyMedium,
            )
          }
        },
      )
    },
  ) { padding ->
    LazyColumn(modifier = Modifier.padding(padding)) {
      items(state.tags.itemCount) { index ->
        val tag = state.tags[index]
        if (tag != null) {
          val isSelected = tag.id in state.selectedTagIds
          ListItem(
            modifier = Modifier.clickable { eventSink(TagsUiEvent.ToggleTag(tag)) },
            headlineContent = { Text(tag.name) },
            leadingContent = { Icon(Icons.Default.Tag, null) },
            trailingContent = {
              if (isSelected) {
                Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = "Selected",
                  tint = MaterialTheme.colorScheme.primary,
                )
              }
            },
          )
        }
      }
    }
  }
}
