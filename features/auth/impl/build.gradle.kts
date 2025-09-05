plugins {
  id("convention.kotlin.multiplatform")
  alias(libs.plugins.ksp)
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(projects.features.auth.api)
      implementation(projects.core.base)
      implementation(projects.core.preferences)
      implementation(projects.data.models)
      implementation(projects.data.linkdingApi) // Brings in ktor deps
    }
  }
}
