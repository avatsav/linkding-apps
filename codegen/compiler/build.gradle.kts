plugins {
    kotlin("jvm")
}

dependencies {
    implementation(projects.codegen.annotations)
    implementation(libs.kotlin.inject.runtime)
    implementation(libs.ksp)
    implementation(libs.kotlinPoet)
    implementation(libs.kotlinPoet.ksp)
}
