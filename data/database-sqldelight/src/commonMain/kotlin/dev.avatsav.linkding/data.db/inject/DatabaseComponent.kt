package dev.avatsav.linkding.data.db.inject

import dev.avatsav.linkding.data.db.Database
import dev.avatsav.linkding.data.db.DatabaseFactory
import dev.avatsav.linkding.data.db.daos.BookmarksDao
import dev.avatsav.linkding.data.db.daos.PagingBookmarksDao
import dev.avatsav.linkding.data.db.daos.SqlDelightBookmarksDao
import dev.avatsav.linkding.data.db.daos.SqlDelightPagingBookmarksDao
import dev.avatsav.linkding.inject.AppScope
import me.tatarka.inject.annotations.Provides

expect interface SqlDelightPlatformComponent

interface SqlDelightComponent : SqlDelightPlatformComponent {
    @AppScope
    @Provides
    fun provideSqlDelightDatabase(
        factory: DatabaseFactory,
    ): Database = factory.createDatabase()
}

interface DatabaseComponent : SqlDelightComponent {

    @AppScope
    @Provides
    fun provideBookmarksDao(bind: SqlDelightBookmarksDao): BookmarksDao = bind

    @AppScope
    @Provides
    fun providePagingBookmarksDao(bind: SqlDelightPagingBookmarksDao): PagingBookmarksDao = bind
}
