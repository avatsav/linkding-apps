// Copyright (C) 2022 Slack Technologies, LLC
// SPDX-License-Identifier: Apache-2.0
plugins {
    kotlin("jvm")
    alias(libs.plugins.ksp)
}

dependencies {
    compileOnly(libs.ksp)
    compileOnly(libs.kotlin.inject.runtime)
    compileOnly(libs.dagger.anvil.annotations)
    compileOnly(libs.anvil.runtime)
    ksp(libs.autoService.ksp)
    implementation(libs.autoService.annotations)
    implementation(libs.dagger)
    implementation(libs.kotlinPoet)
    implementation(libs.kotlinPoet.ksp)
}
