package dev.avatsav.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

class KotlinMultiplatformPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("org.jetbrains.kotlin.multiplatform")
        }

        extensions.configure<KotlinMultiplatformExtension> {
            jvmToolchain(findVersion("jvmToolchain").toInt())
            applyDefaultHierarchyTemplate()

            jvm()
            if (pluginManager.hasPlugin("com.android.library")) {
                androidTarget()
            }

            iosArm64()
            iosSimulatorArm64()

            targets.withType<KotlinNativeTarget>().configureEach {
                binaries.configureEach {
                    linkerOpts("-lsqlite3")
                }

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
                        }
                    }
                }
            }

            configureKotlin()
            configureKtfmt()
            configureDetekt()
        }
    }
}

internal fun Project.configureKotlin() {
    // Java toolchain configuration is picked up by Kotlin
    configureJavaToolchain()
}
