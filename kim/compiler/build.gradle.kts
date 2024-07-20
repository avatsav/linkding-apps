plugins {
    kotlin("jvm")
}

dependencies {
    implementation(projects.kim.annotations)
    implementation(libs.kotlin.inject.runtime)
    implementation(libs.ksp)
    implementation(libs.kotlinPoet)
    implementation(libs.kotlinPoet.ksp)
}
