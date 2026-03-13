plugins { id("convention.kmp.lib") }

val databaseBackend = providers.gradleProperty("linkding.databaseBackend").orElse("sqldelight")

kotlin {
  sourceSets {
    commonMain.dependencies {
      api(projects.data.database)

      when (databaseBackend.get()) {
        "room" -> api(projects.data.databaseRoom)
        "sqldelight" -> api(projects.data.databaseSqldelight)
        else -> error("Unsupported linkding.databaseBackend: ${databaseBackend.get()}")
      }
    }
  }
}
