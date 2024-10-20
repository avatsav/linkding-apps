package dev.avatsav.linkding.data.db.inject

import android.app.Application
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import dev.avatsav.linkding.data.db.Database
import dev.avatsav.linkding.inject.AppScope
import dev.avatsav.linkding.inject.annotations.SingleIn
import me.tatarka.inject.annotations.Provides

actual interface SqlDelightPlatformComponent {

    @Provides
    @SingleIn(AppScope::class)
    fun provideSqliteDriver(
        application: Application,
    ): SqlDriver = AndroidSqliteDriver(
        schema = Database.Schema,
        context = application,
        name = "linkding.db",
    )
}
