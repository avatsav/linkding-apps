import dev.avatsav.gradle.addKspDependencyForAllTargets

plugins {
  id("convention.kotlin.multiplatform")
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.ksp)
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(projects.core.base)
      api(projects.features.bookmarks.api)

      // Data layer dependencies
      implementation(projects.data.models)
      implementation(projects.data.linkdingApi)
      implementation(projects.data.database)
      implementation(projects.data.databaseSqldelight)

      // External dependencies (internal use only, not exposed)
      implementation(libs.paging.common)
      implementation(libs.kotlinResult)
      implementation(libs.multiplatform.settings)
      implementation(libs.multiplatform.settings.coroutines)
      implementation(libs.kotlin.serialization.json)
      implementation(libs.kotlin.datetime)
    }
  }
}

addKspDependencyForAllTargets(libs.circuit.codegen)
