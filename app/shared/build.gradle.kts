import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
  id("convention.kmp.lib")
  id("convention.compose")
  alias(libs.plugins.ksp)
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      // Core infrastructure - needed by apps
      api(projects.core.base)
      api(projects.core.di)
      api(projects.core.preferences)
      api(projects.core.connectivity)

      api(projects.data.models)
      api(projects.data.linkdingApi)
      api(projects.data.databaseSqldelight)

      api(projects.ui.theme)
      api(projects.ui.navigation)

      api(projects.features.auth.api)
      api(projects.features.auth.impl)
      api(projects.features.auth.ui)

      api(projects.features.bookmarks.api)
      api(projects.features.bookmarks.impl)
      api(projects.features.bookmarks.ui)

      api(projects.features.settings.api)
      api(projects.features.settings.impl)
      api(projects.features.settings.ui)
    }

    targets.withType<KotlinNativeTarget>().configureEach {
      binaries.framework {
        isStatic = true
        baseName = "LinkdingKt"
        export(projects.data.models)
      }
    }
  }
}
