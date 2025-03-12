import dev.avatsav.gradle.configureParcelize

plugins {
  id("convention.android.library")
  id("convention.kotlin.multiplatform")
  alias(libs.plugins.kotlin.parcelize)
}

kotlin { configureParcelize() }

android { namespace = "dev.avatsav.linkding.parcelize" }
