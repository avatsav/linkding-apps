plugins {
  `kotlin-dsl`
  alias(libs.plugins.ktfmt)
}

dependencies {
  compileOnly(libs.android.gradlePlugin)
  compileOnly(libs.kotlin.gradlePlugin)
  compileOnly(libs.compose.gradlePlugin)
  compileOnly(libs.composeCompiler.gradlePlugin)
  compileOnly(libs.ksp.gradlePlugin)
  compileOnly(libs.ktfmt.gradlePlugin)
  compileOnly(libs.detekt.gradlePlugin)
  compileOnly(libs.metro.gradlePlugin)
  compileOnly(libs.gradleVersions.gradlePlugin)
}

java { toolchain { languageVersion.set(JavaLanguageVersion.of(libs.versions.jvmToolchain.get())) } }

ktfmt { googleStyle() }

gradlePlugin {
  plugins {
    register("kotlinMultiplatform") {
      id = "convention.kotlin.multiplatform"
      implementationClass = "dev.avatsav.gradle.KotlinMultiplatformPlugin"
    }

    register("composeMultiplatform") {
      id = "convention.compose"
      implementationClass = "dev.avatsav.gradle.ComposePlugin"
    }

    register("androidApplication") {
      id = "convention.android.application"
      implementationClass = "dev.avatsav.gradle.AndroidApplicationPlugin"
    }

    register("androidLibrary") {
      id = "convention.android.library"
      implementationClass = "dev.avatsav.gradle.AndroidLibraryPlugin"
    }
  }
}
