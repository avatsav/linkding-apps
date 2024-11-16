// Copyright (C) 2022 Slack Technologies, LLC
// SPDX-License-Identifier: Apache-2.0
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    id("convention.android.library")
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    // region KMP Targets
    androidTarget()
    jvm()
    // Anvil/Dagger does not support iOS targets
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    watchosArm32()
    watchosArm64()
    watchosX64()
    watchosSimulatorArm64()
    tvosArm64()
    tvosX64()
    tvosSimulatorArm64()
    macosX64()
    macosArm64()
    linuxArm64()
    linuxX64()
    // endregion

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    applyDefaultHierarchyTemplate {
        common {
            group("commonJvm") {
                withJvm()
                withAndroidTarget()
            }
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                compileOnly(libs.anvil.runtime)
                api(libs.circuit.runtime)
            }
        }
        named("commonJvmMain") { dependencies { compileOnly(libs.hilt) } }
        nativeMain {
            dependencies {
                compileOnly(libs.anvil.runtime)
                api(libs.anvil.runtime)
            }
        }
    }

    targets.configureEach {
        compilations.configureEach {
            compileTaskProvider.configure {
                compilerOptions { freeCompilerArgs.add("-Xexpect-actual-classes") }
            }
        }
    }
}

android {
    namespace = "com.slack.circuit.codegen.annotations"
    compileSdk = 35
    defaultConfig {
        consumerProguardFiles(
            "src/commonJvmMain/resources/META-INF/proguard/circuit-codegen-annotations.pro",
        )
    }
}
