package dev.avatsav.linkding.data.db.inject

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import dev.avatsav.linkding.data.db.Database
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

actual interface SqlDelightPlatformComponent {

    @Provides
    @SingleIn(AppScope::class)
    fun provideSqliteDriver(): SqlDriver = NativeSqliteDriver(
        schema = Database.Schema,
        name = "linkding.db",
    )
}
