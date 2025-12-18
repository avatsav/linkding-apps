plugins { id("convention.kmp.lib") }

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
