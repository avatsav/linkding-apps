import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    id("convention.android.library")
    id("convention.kotlin.multiplatform")
    id("convention.compose")
    alias(libs.plugins.kotlin.parcelize)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.circuit.runtime)
        }
    }

    // https://issuetracker.google.com/issues/315775835#comment18
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            freeCompilerArgs.addAll(
                "-P",
                "plugin:org.jetbrains.kotlin.parcelize:additionalAnnotation=dev.avatsav.linkding.ui.Parcelize",
            )
        }
    }
}

android {
    namespace = "dev.avatsav.linkding.ui.screens"
}
