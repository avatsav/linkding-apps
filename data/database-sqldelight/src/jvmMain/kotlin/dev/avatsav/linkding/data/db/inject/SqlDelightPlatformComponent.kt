package dev.avatsav.linkding.data.db.inject

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import dev.avatsav.linkding.data.db.Database
import dev.avatsav.linkding.inject.AppScope
import me.tatarka.inject.annotations.Provides

actual interface SqlDelightPlatformComponent {

    @Provides
    @AppScope
    fun provideSqliteDriver(): SqlDriver {
        val driver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        Database.Schema.create(driver)
        return driver
    }
}
