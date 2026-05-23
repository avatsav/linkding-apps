plugins { id("convention.kmp.lib") }

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(libs.kotlin.coroutines.core)
    }
  }
}
