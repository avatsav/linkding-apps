package dev.avatsav.linkding.data.db.room

import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import dev.avatsav.linkding.AppCoroutineDispatchers
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import java.io.File

private const val AppDirectoryName = "Linkding"
private const val DatabaseName = "linkding-room.db"

@ContributesTo(AppScope::class)
actual interface RoomPlatformProviders {
  @Provides
  @SingleIn(AppScope::class)
  fun provideRoomDatabase(dispatchers: AppCoroutineDispatchers): LinkdingRoomDatabase {
    val databaseFile = File(appSupportDirectory(), DatabaseName)
    return Room
      .databaseBuilder<LinkdingRoomDatabase>(name = databaseFile.absolutePath)
      .setDriver(BundledSQLiteDriver())
      .setQueryCoroutineContext(dispatchers.io)
      .build()
  }
}

private fun appSupportDirectory(): File {
  val home = System.getProperty("user.home")
  val osName = System.getProperty("os.name").lowercase()

  val directory =
    when {
      osName.contains("mac") -> File(home, "Library/Application Support/$AppDirectoryName")
      osName.contains("win") -> {
        val appData = System.getenv("APPDATA") ?: File(home, "AppData/Roaming").path
        File(appData, AppDirectoryName)
      }
      else -> {
        val dataHome = System.getenv("XDG_DATA_HOME") ?: File(home, ".local/share").path
        File(dataHome, AppDirectoryName.lowercase())
      }
    }

  directory.mkdirs()
  return directory
}
