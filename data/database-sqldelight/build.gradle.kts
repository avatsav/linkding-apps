plugins {
  id("convention.android.library")
  id("convention.kotlin.multiplatform")
  alias(libs.plugins.sqldelight)
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(projects.core.base)
      api(projects.data.database) // Implements database contracts
      implementation(libs.kotlin.atomicfu)
      implementation(libs.sqldelight.coroutines)
      implementation(libs.sqldelight.primitive)
      implementation(projects.data.models) // Internal use, models exposed via database contract
      implementation(libs.kotlin.datetime)
      implementation(libs.kotlin.coroutines.core)
      implementation(libs.paging.common)
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

android { namespace = "dev.avatsav.linkding.data.db" }
