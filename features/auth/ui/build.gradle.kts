plugins {
  id("convention.kmp.lib")
  id("convention.compose")
  alias(libs.plugins.ksp)
}

kotlin {
  sourceSets {
    jvmTest.dependencies {
      implementation(libs.kotlin.test)
      implementation(libs.kotlin.coroutines.test)
      implementation(libs.kotlin.coroutines.swing)
      implementation(compose.desktop.currentOs)
      implementation(libs.compose.ui.test.junit4)
    }
    commonMain.dependencies {
      implementation(projects.features.auth.api)
      implementation(projects.core.base)
      implementation(projects.core.connectivity)
      implementation(projects.core.di)
      implementation(projects.core.presenter)
      implementation(projects.ui.compose)
      implementation(projects.ui.navigation)
      implementation(libs.compose.components.resources)
    }
  }
}
