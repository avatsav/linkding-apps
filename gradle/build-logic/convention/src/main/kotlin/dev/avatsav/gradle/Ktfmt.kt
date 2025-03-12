package dev.avatsav.gradle

import com.ncorti.ktfmt.gradle.KtfmtExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

internal fun Project.configureKtfmt() {
    pluginManager.apply("com.ncorti.ktfmt.gradle")

    configure<KtfmtExtension> {
        googleStyle()
        removeUnusedImports.set(true)
        manageTrailingCommas.set(true)
    }

    tasks.named("ktfmtFormat") {
        // Skip ktfmt for thirdparty module
        if (path.startsWith(":thirdparty")) return@named
    }

}
