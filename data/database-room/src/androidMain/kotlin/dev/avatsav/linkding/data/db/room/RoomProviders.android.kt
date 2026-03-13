package dev.avatsav.linkding.data.db.room

import android.app.Application
import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import dev.avatsav.linkding.AppCoroutineDispatchers
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

private const val DatabaseName = "linkding-room.db"

@ContributesTo(AppScope::class)
actual interface RoomPlatformProviders {
  @Provides
  @SingleIn(AppScope::class)
  fun provideRoomDatabase(
    application: Application,
    dispatchers: AppCoroutineDispatchers,
  ): LinkdingRoomDatabase {
    val dbFile = application.getDatabasePath(DatabaseName)
    return Room
      .databaseBuilder<LinkdingRoomDatabase>(context = application, name = dbFile.absolutePath)
      .setDriver(BundledSQLiteDriver())
      .setQueryCoroutineContext(dispatchers.io)
      .build()
  }
}
