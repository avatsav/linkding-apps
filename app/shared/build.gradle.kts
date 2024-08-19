import dev.avatsav.gradle.addKspDependencyForAllTargets
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id("convention.android.library")
    id("convention.kotlin.multiplatform")
    id("convention.compose")
    alias(libs.plugins.ksp)
}

android {
    namespace = "dev.avatsav.linkding.shared"
}

kotlin {
    sourceSets {

        // Add ksp source sets to use kotlin-inject generated classes
        iosArm64Main {
            kotlin.srcDir("build/generated/ksp/iosArm64/iosArm64Main/kotlin")
        }

        iosSimulatorArm64Main {
            kotlin.srcDir("build/generated/ksp/iosSimulatorArm64/iosSimulatorArm64Main/kotlin")
        }

        commonMain.dependencies {
            api(projects.core.base)
            api(projects.core.logging)
            api(projects.core.preferences)
            api(projects.core.connectivity)
            api(projects.data.network)
            api(projects.data.linkdingApi)
            api(projects.data.auth)
            api(projects.data.bookmarks)
            api(projects.data.databaseSqldelight)
            api(projects.domain)
            api(projects.ui.theme)
            api(projects.features.setup)
            api(projects.features.bookmarks)
            api(projects.features.addBookmark)
            api(projects.features.settings)
            api(projects.features.tags)

            api(libs.circuit.foundation)
            api(libs.circuit.runtime)
            api(libs.circuit.foundation)
            api(libs.circuit.overlay)
            api(libs.circuit.runtime)
            api(libs.circuitx.gestureNavigation)
            api(libs.kermit)
        }

        targets.withType<KotlinNativeTarget>().configureEach {
            binaries.framework {
                isStatic = true
                baseName = "LinkdingKt"
            }
        }
    }
}

ksp {
    arg("me.tatarka.inject.generateCompanionExtensions", "true")
}

addKspDependencyForAllTargets(libs.kotlin.inject.compiler)
