package dev.avatsav.gradle

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

class KmpLibraryPlugin : Plugin<Project> {
  @OptIn(ExperimentalKotlinGradlePluginApi::class)
  override fun apply(target: Project) =
    with(target) {
      with(pluginManager) {
        apply("com.android.kotlin.multiplatform.library")
        apply("org.jetbrains.kotlin.multiplatform")
        apply("org.jetbrains.kotlin.plugin.serialization")
        apply("dev.zacsweers.metro")
      }
      configureKmpPlugin()
    }
}

@OptIn(ExperimentalKotlinGradlePluginApi::class)
private fun Project.configureKmpPlugin() {
  extensions.configure<KotlinMultiplatformExtension> {
    jvmToolchain(findVersion("jvmToolchain").toInt())

    jvm()
    iosArm64()
    iosSimulatorArm64()

    extensions.configure<KotlinMultiplatformAndroidLibraryExtension> {
      compileSdk = findVersion("compileSdk").toInt()
      minSdk = findVersion("minSdk").toInt()
      namespace = "dev.avatsav.linkding.${path.substring(1).replace(":", ".").replace("-", "_")}"

      androidResources { enable = true }
      withHostTest { isIncludeAndroidResources = true }
      withDeviceTestBuilder { sourceSetTreeName = KotlinSourceSetTree.test.name }
        .configure { instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" }
    }

    targets.withType<KotlinNativeTarget>().configureEach {
      binaries.configureEach { linkerOpts("-lsqlite3") }

      compilations.configureEach {
        compileTaskProvider.configure {
          compilerOptions {
            freeCompilerArgs.addAll(
              "-opt-in=kotlinx.cinterop.ExperimentalForeignApi",
              "-opt-in=kotlinx.cinterop.BetaInteropApi",
            )
          }
        }
      }
    }

    targets.configureEach {
      compilations.configureEach {
        compileTaskProvider.configure {
          compilerOptions {
            freeCompilerArgs.add("-Xexpect-actual-classes")
            optIn.add("kotlin.time.ExperimentalTime")
          }
        }
      }
    }

    configureJavaToolchain()
    configureKtfmt()
    configureDetekt()
  }
}
