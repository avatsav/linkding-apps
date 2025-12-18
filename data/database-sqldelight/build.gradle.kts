plugins {
  alias(libs.plugins.android.kmp.library)
  alias(libs.plugins.sqldelight)
  id("convention.kmp.lib")
}

kotlin {
  androidLibrary {
    namespace = "dev.avatsav.linkding.data.db"
    compileSdk = 36
    minSdk = 30
  }

  sourceSets {
    commonMain.dependencies {
      implementation(projects.core.base)
      api(projects.data.database) // Implements database contracts
      api(libs.sqldelight.coroutines)
      implementation(libs.sqldelight.primitive)
      implementation(projects.data.models) // Internal use, models exposed via database contract
      implementation(libs.kotlin.datetime)
    }
    androidMain.dependencies { implementation(libs.sqldelight.android) }
    iosMain.dependencies {
      // Required for iOS build:
      // https://github.com/cashapp/sqldelight/issues/4888#issuecomment-1846036472
      implementation(libs.stately)
      implementation(libs.sqldelight.native)
    }
    jvmMain.dependencies { implementation(libs.sqldelight.sqlite) }
  }
}

sqldelight { databases { create("Database") { packageName = "dev.avatsav.linkding.data.db" } } }
