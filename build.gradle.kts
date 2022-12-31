@file:Suppress("UnstableApiUsage", "DSL_SCOPE_VIOLATION")

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.BasePlugin
import com.diffplug.gradle.spotless.SpotlessExtension
import dagger.hilt.android.plugin.HiltExtension
import org.jetbrains.kotlin.gradle.plugin.KaptExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.lint) apply false
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.native.cocoapods) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.kmpNativeCoroutines) apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://androidx.dev/storage/compose-compiler/repository")
    }
    apply(plugin = rootProject.libs.plugins.spotless.get().pluginId)
    configure<SpotlessExtension> {
        kotlin {
            target("**/*.kt")
            targetExclude("$buildDir/**/*.kt")
            targetExclude("bin/**/*.kt")
            ktlint(libs.versions.ktlint.get()).editorConfigOverride(
                mapOf(
                    "ij_kotlin_imports_layout" to "*",
                    "ij_kotlin_allow_trailing_comma" to "true",
                    "ij_kotlin_allow_trailing_comma_on_call_site" to "true",
                ),
            )
        }
        kotlinGradle {
            target("**/*.kts")
            targetExclude("$buildDir/**/*.kts")
            ktlint(libs.versions.ktlint.get()).editorConfigOverride(
                mapOf(
                    "ij_kotlin_imports_layout" to "*",
                    "ij_kotlin_allow_trailing_comma" to "true",
                    "ij_kotlin_allow_trailing_comma_on_call_site" to "true",
                ),
            )
        }
    }

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_11.toString()
            freeCompilerArgs = freeCompilerArgs + listOf(
                "-P",
                "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=true"
            )
        }
    }
    plugins.withId(rootProject.libs.plugins.hilt.get().pluginId) {
        extensions.getByType<HiltExtension>().enableAggregatingTask = true
    }
    plugins.withId(rootProject.libs.plugins.kotlin.kapt.get().pluginId) {
        extensions.getByType<KaptExtension>().correctErrorTypes = true
    }
    plugins.withType<BasePlugin>().configureEach {
        extensions.configure<BaseExtension> {
            compileSdkVersion(libs.versions.compileSdk.get().toInt())
            defaultConfig {
                minSdk = libs.versions.minSdk.get().toInt()
                targetSdk = libs.versions.targetSdk.get().toInt()
            }
            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_11
                targetCompatibility = JavaVersion.VERSION_11
            }
        }
    }

}