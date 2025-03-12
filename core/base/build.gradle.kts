import dev.avatsav.gradle.addKspDependencyForAllTargets

plugins {
  id("convention.kotlin.multiplatform")
  alias(libs.plugins.ksp)
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      api(libs.kotlin.atomicfu)
      api(libs.kotlin.coroutines.core)
      api(libs.kotlin.inject.runtime)
      api(libs.anvil.runtime)
      api(libs.anvil.runtime.optional)
      api(libs.kermit)
      api(libs.kotlinResult)
      api(libs.kotlinResultCoroutines)
      implementation(libs.uuid)
    }
  }
}

addKspDependencyForAllTargets(libs.kotlin.inject.compiler)

addKspDependencyForAllTargets(libs.anvil.compiler)
