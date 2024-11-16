import dev.avatsav.gradle.addKspDependencyForAllTargets

plugins {
    id("convention.android.library")
    id("convention.kotlin.multiplatform")
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.ksp)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.base)
            api(projects.data.database)
            implementation(libs.kotlin.atomicfu)
            implementation(libs.sqldelight.coroutines)
            implementation(libs.sqldelight.primitive)
            api(projects.data.models)
            api(libs.kotlin.datetime)
            api(libs.kotlin.coroutines.core)
            api(libs.paging.common)
        }
        androidMain.dependencies {
            implementation(libs.sqldelight.android)
        }
        iosMain.dependencies {
            // Required for iOS build: https://github.com/cashapp/sqldelight/issues/4888#issuecomment-1846036472
            implementation(libs.stately)
            implementation(libs.sqldelight.native)
        }
        jvmMain.dependencies {
            implementation(libs.sqldelight.sqlite)
        }
    }
}

sqldelight {
    databases {
        create("Database") {
            packageName = "dev.avatsav.linkding.data.db"
        }
    }
}

android {
    namespace = "dev.avatsav.linkding.data.db"
}

addKspDependencyForAllTargets(libs.anvil.compiler)
