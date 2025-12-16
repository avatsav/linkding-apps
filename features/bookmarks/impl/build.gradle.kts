plugins {
  id("convention.kmp.lib")
  alias(libs.plugins.ksp)
}

kotlin {
  android {
    namespace = "dev.avatsav.linkding.bookmarks.impl"
    compileSdk { version = release(36) }
  }
  sourceSets {
    commonMain.dependencies {
      implementation(projects.core.base)
      implementation(projects.core.di)
      api(projects.features.bookmarks.api)

      // Data layer dependencies
      implementation(projects.data.linkdingApi)
      implementation(projects.data.database)
      implementation(projects.data.databaseSqldelight)

      // External dependencies (internal use only, not exposed)
      implementation(libs.multiplatform.settings)
      implementation(libs.multiplatform.settings.coroutines)
      implementation(libs.kotlin.serialization.json)
      implementation(libs.kotlin.datetime)
    }
  }
}
