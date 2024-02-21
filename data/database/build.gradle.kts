plugins {
    id("convention.android.library")
    id("convention.kotlin.multiplatform")
    alias(libs.plugins.sqldelight)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.kotlin.coroutines.core)
            api(projects.core.base)
            api(projects.core.logging)
            api(projects.data.models)

            api(libs.kotlin.datetime)
            api(libs.kotlin.atomicfu)
            api(libs.kotlin.coroutines.core)

            implementation(libs.sqldelight.coroutines)
            implementation(libs.sqldelight.paging)
            implementation(libs.sqldelight.primitive)

            implementation(libs.kotlin.inject.runtime)
        }
        androidMain.dependencies {
            implementation(libs.sqldelight.android)
        }
        iosMain.dependencies {
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
