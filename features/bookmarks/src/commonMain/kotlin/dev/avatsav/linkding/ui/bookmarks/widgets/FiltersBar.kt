package dev.avatsav.linkding.ui.bookmarks.widgets

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.slack.circuit.overlay.LocalOverlayHost
import dev.avatsav.linkding.data.model.BookmarkCategory
import dev.avatsav.linkding.data.model.Tag
import dev.avatsav.linkding.ui.TagsScreenResult
import dev.avatsav.linkding.ui.compose.widgets.TagInputChip
import dev.avatsav.linkding.ui.tags.mapToTag
import dev.avatsav.linkding.ui.tags.showTagsBottomSheet
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FiltersBar(
    selectedCategory: BookmarkCategory,
    onCategorySelected: (BookmarkCategory) -> Unit,
    selectedTags: List<Tag>,
    onTagSelected: (Tag) -> Unit,
    onTagRemoved: (Tag) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val overlayHost = LocalOverlayHost.current

    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item(selectedCategory) {
            CategoryFilter(
                modifier = Modifier.animateItemPlacement(),
                selected = selectedCategory,
                onSelected = onCategorySelected,
            )
        }
        item {
            FilterChip(
                modifier = Modifier.animateItemPlacement(),
                selected = true,
                onClick = {
                    scope.launch {
                        when (val result = overlayHost.showTagsBottomSheet(selectedTags)) {
                            TagsScreenResult.Dismissed -> {}
                            is TagsScreenResult.Selected -> onTagSelected(result.tag.mapToTag())
                        }
                    }
                },
                label = { Text("Tags") },
                trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
            )
        }
        items(selectedTags.size, key = { selectedTags[it].id }) { index ->
            val item = selectedTags[index]
            TagInputChip(
                modifier = Modifier.animateItemPlacement(),
                onClick = { onTagRemoved(item) },
                label = { Text(item.name) },
            )
        }
    }
}

private val bookmarkCategories = BookmarkCategory.entries.toImmutableList()

@Composable
private fun CategoryFilter(
    modifier: Modifier = Modifier,
    selected: BookmarkCategory,
    onSelected: (BookmarkCategory) -> Unit,
) {
    var showMenu by remember { mutableStateOf(false) }

    FilterChip(
        modifier = modifier,
        selected = true,
        onClick = { showMenu = showMenu.not() },
        label = { Text(selected.name) },
        trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
    )
    DropdownMenu(
        offset = DpOffset(16.dp, 0.dp),
        expanded = showMenu,
        onDismissRequest = { showMenu = false },
    ) {
        bookmarkCategories.forEach { category ->
            DropdownMenuItem(
                onClick = {
                    onSelected(category)
                    showMenu = false
                },
                text = { Text(category.name) },
            )
        }
    }
}
