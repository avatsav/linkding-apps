plugins {
    id("convention.android.library")
    id("convention.kotlin.multiplatform")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            //put your multiplatform dependencies here
        }
        commonTest.dependencies {
            //put your multiplatform test dependencies here
        }
    }
}


android {
    namespace = "dev.avatsav.linkding.api"
}
