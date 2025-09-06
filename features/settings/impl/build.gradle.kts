plugins { id("convention.kotlin.multiplatform") }

kotlin {
  sourceSets {
    commonMain.dependencies {
      api(projects.features.settings.api)
      implementation(projects.core.preferences)
      implementation(projects.core.base)
    }
  }
}
