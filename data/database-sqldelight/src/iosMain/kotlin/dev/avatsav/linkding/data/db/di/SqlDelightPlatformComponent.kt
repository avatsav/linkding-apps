package dev.avatsav.linkding.data.db.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import dev.avatsav.linkding.data.db.Database
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

actual interface SqlDelightPlatformComponent {

  @Provides
  @SingleIn(AppScope::class)
  fun provideSqliteDriver(): SqlDriver =
    NativeSqliteDriver(schema = Database.Schema, name = "linkding.db")
}
