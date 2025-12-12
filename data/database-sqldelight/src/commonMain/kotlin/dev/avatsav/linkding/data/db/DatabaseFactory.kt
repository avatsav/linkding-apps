package dev.avatsav.linkding.data.db

import app.cash.sqldelight.db.SqlDriver
import dev.avatsav.linkding.data.db.adapters.InstantColumnAdapter
import dev.avatsav.linkding.data.db.adapters.StringSetColumnAdapter
import dev.zacsweers.metro.Inject

@Inject
class DatabaseFactory(private val sqlDriver: SqlDriver) {

  fun createDatabase(): Database =
    Database(
      driver = sqlDriver,
      bookmarksAdapter =
        Bookmarks.Adapter(
          tagsAdapter = StringSetColumnAdapter,
          addedAdapter = InstantColumnAdapter,
          modifiedAdapter = InstantColumnAdapter,
        ),
      search_historyAdapter = Search_history.Adapter(modifiedAdapter = InstantColumnAdapter),
    )
}
