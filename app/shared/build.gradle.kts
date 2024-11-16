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
        commonMain.dependencies {
            api(projects.core.base)
            api(projects.core.preferences)
            api(projects.core.connectivity)
            api(projects.data.models)
            api(projects.data.network)
            api(projects.data.linkdingApi)
            api(projects.data.bookmarks)
            api(projects.data.databaseSqldelight)

            api(projects.domain)
            api(projects.ui.theme)

            api(projects.features.auth.api)
            api(projects.features.auth.impl)
            api(projects.features.auth.ui)

            api(projects.features.bookmarks)
            api(projects.features.settings)

            api(libs.circuit.foundation)
            api(libs.circuit.runtime)
            api(libs.circuit.overlay)
            api(libs.circuit.runtime)
            api(libs.circuitx.gestureNavigation)

            implementation(libs.kotlin.inject.runtime)
            implementation(libs.anvil.runtime)
            implementation(libs.anvil.runtime.optional)
            implementation(libs.circuit.codegen.annotations)
            implementation(projects.codegen.annotations)
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

ksp {
    arg("circuit.codegen.mode", "kotlin_inject_anvil")
    arg("kotlin-inject-anvil-contributing-annotations", "com.slack.circuit.codegen.annotations.CircuitInject")
}

addKspDependencyForAllTargets(libs.kotlin.inject.compiler)
addKspDependencyForAllTargets(libs.anvil.compiler)
addKspDependencyForAllTargets(libs.circuit.codegen)
addKspDependencyForAllTargets(projects.codegen.compiler)
