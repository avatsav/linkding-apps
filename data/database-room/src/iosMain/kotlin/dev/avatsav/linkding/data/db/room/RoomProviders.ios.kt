package dev.avatsav.linkding.data.db.room

import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import dev.avatsav.linkding.AppCoroutineDispatchers
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import platform.Foundation.NSFileManager
import platform.Foundation.NSHomeDirectory

private const val DatabaseName = "linkding-room.db"

@ContributesTo(AppScope::class)
actual interface RoomPlatformProviders {
  @Provides
  @SingleIn(AppScope::class)
  fun provideRoomDatabase(dispatchers: AppCoroutineDispatchers): LinkdingRoomDatabase {
    val appSupportPath = NSHomeDirectory() + "/Library/Application Support"
    check(
      NSFileManager.defaultManager.createDirectoryAtPath(
        path = appSupportPath,
        withIntermediateDirectories = true,
        attributes = null,
        error = null,
      )
    ) {
      "Failed to create Room database directory at $appSupportPath"
    }
    val dbFilePath = appSupportPath + "/" + DatabaseName
    return Room
      .databaseBuilder<LinkdingRoomDatabase>(name = dbFilePath)
      .setDriver(BundledSQLiteDriver())
      .setQueryCoroutineContext(dispatchers.io)
      .build()
  }
}
