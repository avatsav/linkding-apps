/**
 * References:
 * - https://github.com/android/nowinandroid/tree/main/build-logic
 * - https://github.com/chrisbanes/tivi/tree/main/gradle/build-logic
 */

plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.spotless.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.composeCompiler.gradlePlugin)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.jvmToolchain.get()))
    }
}

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
