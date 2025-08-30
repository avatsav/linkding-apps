package dev.avatsav.linkding.data.db.inject

import android.app.Application
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import dev.avatsav.linkding.data.db.Database
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.SingleIn

actual interface SqlDelightPlatformComponent {

  @Provides
  @SingleIn(AppScope::class)
  fun provideSqliteDriver(application: Application): SqlDriver =
    AndroidSqliteDriver(schema = Database.Schema, context = application, name = "linkding.db")
}
