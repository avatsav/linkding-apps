package dev.avatsav.linkding.data.db.sqldelight

import app.cash.sqldelight.db.SqlDriver
import dev.avatsav.linkding.data.db.Bookmarks
import dev.avatsav.linkding.data.db.Database
import dev.avatsav.linkding.data.db.sqldelight.adapters.InstantColumnAdapter
import dev.avatsav.linkding.data.db.sqldelight.adapters.StringSetColumnAdapter
import me.tatarka.inject.annotations.Inject

@Inject
class DatabaseFactory(private val sqlDriver: SqlDriver) {

    fun createDatabase(): Database {
        return Database(
            driver = sqlDriver,
            bookmarksAdapter = Bookmarks.Adapter(
                tagsAdapter = StringSetColumnAdapter,
                addedAdapter = InstantColumnAdapter,
                modifiedAdapter = InstantColumnAdapter,
            ),
        )
    }
}
