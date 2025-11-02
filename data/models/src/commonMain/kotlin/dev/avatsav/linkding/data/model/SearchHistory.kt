package dev.avatsav.linkding.data.model

import kotlin.time.Instant

data class SearchHistory(
  val query: String,
  val bookmarkCategory: BookmarkCategory,
  val selectedTags: List<Tag>,
  val timestamp: Instant,
) {

  fun isSameSearch(other: SearchHistory): Boolean =
    query == other.query &&
      bookmarkCategory == other.bookmarkCategory &&
      selectedTags == other.selectedTags

  fun hasFilterCriteria(): Boolean =
    bookmarkCategory != BookmarkCategory.All || selectedTags.isNotEmpty()
}
