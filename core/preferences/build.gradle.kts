plugins {
  alias(libs.plugins.android.kmp.library)
  id("convention.kmp.lib")
}

kotlin {
  androidLibrary {
    namespace = "dev.avatsav.linkding.prefs"
    compileSdk = 36
    minSdk = 30
  }
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
