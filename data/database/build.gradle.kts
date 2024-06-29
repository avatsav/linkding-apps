plugins {
    id("convention.android.library")
    id("convention.kotlin.multiplatform")
    alias(libs.plugins.sqldelight)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.base)
            implementation(projects.core.logging)
            implementation(libs.kotlin.atomicfu)
            implementation(libs.sqldelight.coroutines)
            implementation(libs.sqldelight.primitive)
            api(projects.data.models)
            api(libs.kotlin.datetime)
            api(libs.kotlin.coroutines.core)
            api(libs.kotlin.inject.runtime)
            api(libs.paging.common)
        }
        androidMain.dependencies {
            implementation(libs.sqldelight.android)
        }
        iosMain.dependencies {
            // Required for iOS build: https://github.com/cashapp/sqldelight/issues/4888#issuecomment-1846036472
            val statelyVersion = "2.0.7"
            implementation("co.touchlab:stately-common:$statelyVersion")
            implementation("co.touchlab:stately-isolate:$statelyVersion")
            implementation("co.touchlab:stately-iso-collections:$statelyVersion")
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
