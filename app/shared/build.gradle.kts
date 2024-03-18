@file:Suppress("UnstableApiUsage", "DSL_SCOPE_VIOLATION")

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
            api(projects.core.logging)
            api(projects.core.preferences)
            api(projects.core.connectivity)
            api(projects.linkding.bind)
            api(projects.domain)
            api(projects.data.bookmarks)
            api(projects.data.database)

            api(projects.ui.core.theme)
            api(projects.ui.root)
            api(projects.ui.setup)
            api(projects.ui.bookmarks)
            api(projects.ui.addBookmark)
            api(projects.ui.settings)
        }
        targets.withType<KotlinNativeTarget>().configureEach {
            binaries.framework {
                isStatic = true
                baseName = "LinkdingKt"
                export(projects.ui.root)
            }
        }
    }
}

ksp {
    arg("me.tatarka.inject.generateCompanionExtensions", "true")
}

addKspDependencyForAllTargets(libs.kotlin.inject.compiler)
