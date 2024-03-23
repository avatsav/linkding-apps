package dev.avatsav.linkding.ui.bookmarks

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import com.slack.circuit.overlay.OverlayHost
import dev.avatsav.linkding.data.model.Tag
import dev.avatsav.linkding.ui.bookmarks.TagPickerResult.Dismissed
import dev.avatsav.linkding.ui.compose.none
import dev.avatsav.linkding.ui.compose.widgets.BottomSheetOverlay
import kotlinx.collections.immutable.ImmutableList

suspend fun OverlayHost.showTagPicker(tags: ImmutableList<Tag>): TagPickerResult {
    return show<TagPickerResult>(
        BottomSheetOverlay(
            sheetShape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            model = tags,
            dragHandle = null,
            onDismiss = { Dismissed },
        ) { toSelect, overlayNavigator ->
            TagsContent(
                tags = toSelect,
                onSelected = { overlayNavigator.finish(TagPickerResult.Selected(it)) },
                onClosed = { overlayNavigator.finish(TagPickerResult.Closed) },
            )
        },
    )
}

sealed interface TagPickerResult {
    data class Selected(val tag: Tag) : TagPickerResult
    data object Closed : TagPickerResult
    data object Dismissed : TagPickerResult
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagsContent(
    tags: ImmutableList<Tag>,
    onSelected: (Tag) -> Unit,
    onClosed: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets.Companion.none,
                title = { Text("Tags") },
                navigationIcon = {
                    IconButton(onClick = { onClosed() }) {
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
            items(tags.size) { index ->
                val tag = tags[index]
                ListItem(
                    modifier = Modifier.clickable { onSelected(tag) },
                    headlineContent = { Text(tag.name) },
                    leadingContent = { Text("#") },
                )
            }
        }
    }
}
