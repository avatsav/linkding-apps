package dev.avatsav.linkding.bookmarks.ui.list.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import dev.avatsav.linkding.data.model.BookmarkCategory
import dev.avatsav.linkding.data.model.Tag
import dev.avatsav.linkding.ui.compose.widgets.TagInputChip

@Composable
fun FiltersBar(
  selectedCategory: BookmarkCategory,
  onSelectCategory: (BookmarkCategory) -> Unit,
  selectedTags: List<Tag>,
  onOpenTagSelector: () -> Unit,
  onRemoveTag: (Tag) -> Unit,
  modifier: Modifier = Modifier,
) {
  LazyRow(
    modifier = modifier,
    contentPadding = PaddingValues(horizontal = 16.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    item(selectedCategory) {
      BookmarkCategoryFilterChip(
        modifier = Modifier.animateItem(),
        selected = selectedCategory,
        onSelect = onSelectCategory,
      )
    }
    item("tag_chip") {
      val hasTags = selectedTags.isNotEmpty()
      AssistChip(
        modifier = Modifier.animateItem(),
        onClick = onOpenTagSelector,
        label = { Text("Tags") },
        colors =
          if (!hasTags) AssistChipDefaults.assistChipColors()
          else AssistChipDefaults.elevatedAssistChipColors(),
        elevation =
          if (!hasTags) AssistChipDefaults.assistChipElevation()
          else AssistChipDefaults.elevatedAssistChipElevation(),
        border = if (!hasTags) AssistChipDefaults.assistChipBorder(true) else null,
        leadingIcon = { Icon(Icons.Default.Tag, null) },
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

private val bookmarkCategories = BookmarkCategory.entries

@Composable
private fun LazyItemScope.BookmarkCategoryFilterChip(
  selected: BookmarkCategory,
  onSelect: (BookmarkCategory) -> Unit,
  modifier: Modifier = Modifier,
) {
  var showMenu by remember { mutableStateOf(false) }
  val isSelected = selected == BookmarkCategory.All
  AssistChip(
    modifier = modifier,
    onClick = { showMenu = showMenu.not() },
    label = { Text(selected.name) },
    colors =
      if (isSelected) AssistChipDefaults.assistChipColors()
      else AssistChipDefaults.elevatedAssistChipColors(),
    elevation =
      if (isSelected) AssistChipDefaults.assistChipElevation()
      else AssistChipDefaults.elevatedAssistChipElevation(),
    border = if (isSelected) AssistChipDefaults.assistChipBorder(true) else null,
    leadingIcon = { Icon(Icons.Default.FilterList, null) },
    trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
  )
  DropdownMenu(
    offset = DpOffset(16.dp, 0.dp),
    expanded = showMenu,
    onDismissRequest = { showMenu = false },
  ) {
    bookmarkCategories.forEach { filter ->
      DropdownMenuItem(
        onClick = {
          onSelect(filter)
          showMenu = false
        },
        text = { Text(filter.name) },
      )
    }
  }
}
