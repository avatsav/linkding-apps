plugins {
  id("convention.android.library")
  id("convention.kotlin.multiplatform")
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      api(libs.kotlin.atomicfu)
      api(libs.kotlin.coroutines.core)
      api(libs.kermit)
      api(libs.kotlinResult)
      api(libs.kotlinResultCoroutines)
      api(libs.paging.common)
      implementation(libs.uuid)
    }
  }
}

android { namespace = "dev.avatsav.linkding" }
