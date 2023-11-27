plugins {
    id("convention.android.library")
    id("convention.kotlin.multiplatform")
    alias(libs.plugins.sqldelight)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.core.base)
                implementation(libs.sqldelight.coroutines)
                implementation(libs.sqldelight.paging)
                implementation(libs.sqldelight.primitive)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.sqldelight.android)
            }
        }

        jvmMain {
            dependencies {
                implementation(libs.sqldelight.sqlite)
            }
        }

        iosMain {
            dependencies {
                implementation(libs.sqldelight.native)
            }
        }
    }
}

android {
    namespace = "dev.avatsav.linkding.db"
}

sqldelight {
    databases {
        create("LinkdingDatabase") {
            packageName = "dev.avatsav.linkding.data.db"
        }
    }
}

