@file:Suppress("UnstableApiUsage", "DSL_SCOPE_VIOLATION")

plugins {
    id("convention.android.application")
    id("convention.compose")
}

android {
    namespace = "dev.avatsav.linkding.android"

    defaultConfig {
        applicationId = "dev.avatsav.linkding.android"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
    }

    packaging {
        resources.excludes += setOf(
            "META-INF/proguard/*",
            "/*.properties",
            "fabric/*.properties",
            "META-INF/*.properties",
            "LICENSE*",
            "META-INF/**/previous-compilation-data.bin",
        )
    }

    buildTypes {
        debug {
            versionNameSuffix = "-dev"
            applicationIdSuffix = ".debug"
        }
        release {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles("proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(projects.app.shared)

    implementation(libs.androidx.activity)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.browser)
    implementation(libs.androidx.splashscreen)
    implementation(libs.kotlin.coroutines.android)

    debugImplementation(libs.leakCanary)

    testImplementation(libs.junit)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.androidx.test.rules)
    testImplementation(libs.androidx.test.runner)
    testImplementation(libs.kotlin.coroutines.test)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.test.espresso)
}
