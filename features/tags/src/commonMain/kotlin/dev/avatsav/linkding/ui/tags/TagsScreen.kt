package dev.avatsav.linkding.ui.tags

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Tag
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import dev.avatsav.linkding.inject.UserScope
import dev.avatsav.linkding.ui.TagsScreen
import dev.avatsav.linkding.ui.compose.none

@CircuitInject(TagsScreen::class, UserScope::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Tags(
    state: TagsUiState,
    modifier: Modifier = Modifier,
) {
    // https://issuetracker.google.com/issues/256100927#comment1
    val eventSink = state.eventSink

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets.none,
                title = { Text("Tags") },
                navigationIcon = {
                    IconButton(onClick = { eventSink(TagsUiEvent.Close) }) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(16.dp),
                ),
                scrollBehavior = scrollBehavior,
            )
        },
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(state.tags.itemCount) { index ->
                val tag = state.tags[index]
                if (tag != null) {
                    ListItem(
                        modifier = Modifier.clickable { eventSink(TagsUiEvent.SelectTag(tag)) },
                        headlineContent = { Text(tag.name) },
                        leadingContent = { Icon(Icons.Default.Tag, null) },
                    )
                }
            }
        }
    }
}
