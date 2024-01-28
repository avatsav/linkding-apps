plugins {
    id("convention.android.library")
    id("convention.kotlin.multiplatform")
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.unfurl)
        }
    }
}

android {
    namespace = "dev.avatsav.linkding.unfurl"
}

