package dev.avatsav.linkding.data.db.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import dev.avatsav.linkding.data.db.Database
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

actual interface SqlDelightPlatformProviders {

  @Provides
  @SingleIn(AppScope::class)
  fun provideSqliteDriver(): SqlDriver {
    val driver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    Database.Schema.create(driver)
    return driver
  }
}
