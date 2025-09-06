import dev.avatsav.gradle.addKspDependencyForAllTargets
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
  id("convention.android.library")
  id("convention.kotlin.multiplatform")
  id("convention.compose")
  alias(libs.plugins.ksp)
}

android { namespace = "dev.avatsav.linkding.shared" }

kotlin {
  sourceSets {
    commonMain.dependencies {
      // Core infrastructure - needed by apps
      api(projects.core.base)
      api(projects.core.preferences)
      api(projects.core.connectivity) // DI components need this

      // Data layer - exposed for DI components
      api(projects.data.models) // Export for iOS framework
      api(projects.data.linkdingApi) // DI components need this
      api(projects.data.databaseSqldelight) // DI components need this

      // UI infrastructure - expose theme for apps
      api(projects.ui.theme)

      // Feature modules - expose all for DI wiring
      api(projects.features.auth.api)
      api(projects.features.auth.impl) // DI needs access to components
      api(projects.features.auth.ui)

      api(projects.features.bookmarks.api)
      api(projects.features.bookmarks.impl) // DI needs access to components
      api(projects.features.bookmarks.ui)

      api(projects.features.settings.api)
      api(projects.features.settings.impl) // DI needs access to components
      api(projects.features.settings.ui)

      // Circuit - needed by apps for navigation
      api(libs.circuit.foundation)
      api(libs.circuit.runtime)
      api(libs.circuit.overlay)
      api(libs.circuitx.gestureNavigation)
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

ksp { arg("circuit.codegen.mode", "metro") }

addKspDependencyForAllTargets(libs.circuit.codegen)
