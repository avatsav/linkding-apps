package dev.avatsav.linkding.bookmarks.ui.list.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyItemScope
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
import dev.avatsav.linkding.bookmarks.ui.tags.mapToTag
import dev.avatsav.linkding.bookmarks.ui.tags.showTagsBottomSheet
import dev.avatsav.linkding.data.model.BookmarkCategory
import dev.avatsav.linkding.data.model.Tag
import dev.avatsav.linkding.ui.TagsScreenResult
import dev.avatsav.linkding.ui.compose.widgets.TagInputChip
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

@Composable
fun FiltersBar(
  selectedCategory: BookmarkCategory,
  onSelectCategory: (BookmarkCategory) -> Unit,
  selectedTags: List<Tag>,
  onSelectTag: (Tag) -> Unit,
  onRemoveTag: (Tag) -> Unit,
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
        modifier = Modifier.animateItem(),
        selected = selectedCategory,
        onSelect = onSelectCategory,
      )
    }
    item {
      FilterChip(
        modifier = Modifier.animateItem(),
        selected = true,
        onClick = {
          scope.launch {
            when (val result = overlayHost.showTagsBottomSheet(selectedTags)) {
              TagsScreenResult.Dismissed -> {}
              is TagsScreenResult.Selected -> onSelectTag(result.tag.mapToTag())
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
        modifier = Modifier.animateItem(),
        onClick = { onRemoveTag(item) },
        label = { Text(item.name) },
      )
    }
  }
}

private val bookmarkCategories = BookmarkCategory.entries.toImmutableList()

@Composable
private fun LazyItemScope.CategoryFilter(
  selected: BookmarkCategory,
  onSelect: (BookmarkCategory) -> Unit,
  modifier: Modifier = Modifier,
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
          onSelect(category)
          showMenu = false
        },
        text = { Text(category.name) },
      )
    }
  }
}
