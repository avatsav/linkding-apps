plugins {
  id("convention.kmp.lib")
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(projects.core.base)
      implementation(projects.core.di)
      implementation(projects.data.models)
      api(libs.multiplatform.settings)
      api(libs.multiplatform.settings.coroutines)
    }

    androidMain.dependencies {
      implementation(libs.androidx.core)
      implementation(libs.androidx.preference)
    }
  }
}
