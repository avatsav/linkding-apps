package dev.avatsav.gradle

import com.android.build.gradle.BaseExtension
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
        compileSdkVersion(findVersion("compileSdk").toInt())
        defaultConfig {
            minSdk = findVersion("minSdk").toInt()
            targetSdk = findVersion("targetSdk").toInt()
        }
    }
}

fun Project.configureJavaToolchain() {
    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
}
