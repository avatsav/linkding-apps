package dev.avatsav.linkding.data.db.adapters

import app.cash.sqldelight.ColumnAdapter
import dev.avatsav.linkding.data.model.BookmarkCategory

internal object BookmarkCategoryColumnAdapter : ColumnAdapter<BookmarkCategory, String> {
  override fun encode(value: BookmarkCategory): String = value.name

  override fun decode(databaseValue: String): BookmarkCategory =
    try {
      BookmarkCategory.valueOf(databaseValue)
    } catch (_: IllegalArgumentException) {
      BookmarkCategory.All
    }
}
