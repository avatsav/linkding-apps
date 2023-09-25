import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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
    compileOnly(libs.jetbrains.compose.gradlePlugin)
}

java { toolchain { languageVersion.set(JavaLanguageVersion.of(17)) } }

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

gradlePlugin {
    plugins {
        register("kotlinMultiplatform") {
            id = "convention.kotlin.multiplatform"
            implementationClass = "dev.avatsav.conventions.KotlinMultiplatformPlugin"
        }

        register("composeMultiplatform") {
            id = "convention.compose"
            implementationClass = "dev.avatsav.conventions.ComposePlugin"
        }

        register("androidApplication") {
            id = "convention.android.application"
            implementationClass = "dev.avatsav.conventions.AndroidApplicationPlugin"
        }

        register("androidLibrary") {
            id = "convention.android.library"
            implementationClass = "dev.avatsav.conventions.AndroidLibraryPlugin"
        }

        register("androidTest") {
            id = "convention.android.test"
            implementationClass = "dev.avatsav.conventions.AndroidTestPlugin"
        }

    }
}
