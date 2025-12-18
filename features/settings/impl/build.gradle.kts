plugins { id("convention.kmp.lib") }

kotlin {
  sourceSets {
    commonMain.dependencies {
      api(projects.features.settings.api)
      implementation(projects.core.preferences)
      implementation(projects.core.base)
      implementation(projects.core.di)
    }
  }
}
