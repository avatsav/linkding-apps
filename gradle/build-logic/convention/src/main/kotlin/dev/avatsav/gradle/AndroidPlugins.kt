package dev.avatsav.gradle

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.configure

class AndroidApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("com.android.application")
            apply("org.jetbrains.kotlin.android")
        }
        configureAndroid()
        configureJavaToolchain()
        configureSpotless()
    }
}

class AndroidLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("com.android.library")
        }
        configureAndroid()
        configureJavaToolchain()
    }
}

private fun Project.configureAndroid() {
    extensions.configure<BaseExtension> {
        val targetSdkVersion = findVersion("targetSdk").toInt()

        compileSdkVersion(findVersion("compileSdk").toInt())
        defaultConfig {
            minSdk = findVersion("minSdk").toInt()
            targetSdk = targetSdkVersion
        }
        testOptions {
            if (this@configure is LibraryExtension) {
                targetSdk = targetSdkVersion
            }
            unitTests {
                isIncludeAndroidResources = true
                isReturnDefaultValues = true
            }
        }
    }
}

fun Project.configureJavaToolchain() {
    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(findVersion("jvmToolchain")))
        }
    }
}

/**
 * Workaround to support previews for Composable in the Android target.
 * https://github.com/JetBrains/compose-multiplatform/issues/3499
 */
@Suppress("UnstableApiUsage")
fun Project.configureComposeAndroidPreviews() {
    if (pluginManager.hasPlugin("com.android.library")) {
        extensions.configure<BaseExtension> {
            buildFeatures.compose = true
            composeOptions {
                kotlinCompilerExtensionVersion = findVersion("composeCompiler")
            }
        }
    }
}
