@file:Suppress("UnstableApiUsage", "DSL_SCOPE_VIOLATION")

import dev.avatsav.gradle.addKspDependencyForAllTargets

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
            api(projects.api.linkding)
            api(projects.domain)
            api(projects.data.bookmarks)

            api(projects.ui.common.compose)
            api(projects.ui.root)
            api(projects.ui.setup)
        }
    }
}

ksp {
    arg("me.tatarka.inject.generateCompanionExtensions", "true")
}

addKspDependencyForAllTargets(libs.kotlin.inject.compiler)
