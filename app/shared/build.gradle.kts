
import dev.avatsav.gradle.addKspDependencyForAllTargets
import dev.avatsav.gradle.configureKspForCircuitCodegen
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
        commonMain.dependencies {
            api(projects.core.base)
            api(projects.core.logging)
            api(projects.core.preferences)
            api(projects.core.connectivity)
            api(projects.data.models)
            api(projects.data.network)
            api(projects.data.linkdingApi)
            api(projects.data.auth)
            api(projects.data.bookmarks)
            api(projects.data.databaseSqldelight)
            api(projects.domain)
            api(projects.ui.theme)
            api(projects.features.setup)
//            api(projects.features.bookmarks)
//            api(projects.features.addBookmark)
//            api(projects.features.settings)
//            api(projects.features.tags)

            api(libs.circuit.foundation)
            api(libs.circuit.runtime)
            api(libs.circuit.foundation)
            api(libs.circuit.overlay)
            api(libs.circuit.runtime)
            api(libs.circuitx.gestureNavigation)
            api(libs.kermit)

            implementation(libs.circuit.codegen.annotations)
            implementation(libs.kotlin.inject.runtime)
            implementation(libs.kotlin.inject.anvil.runtime)
            implementation(libs.kotlin.inject.anvil.runtime.optional)
        }

        targets.withType<KotlinNativeTarget>().configureEach {
            binaries.framework {
                isStatic = true
                baseName = "LinkdingKt"
                export(projects.data.models)
            }
        }
    }
}

configureKspForCircuitCodegen()
addKspDependencyForAllTargets(libs.circuit.codegen)
addKspDependencyForAllTargets(libs.kotlin.inject.compiler)
addKspDependencyForAllTargets(libs.kotlin.inject.anvil.compiler)
