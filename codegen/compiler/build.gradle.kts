plugins {
    kotlin("jvm")
}

dependencies {
    implementation(projects.codegen.annotations)
    implementation(libs.ksp)
    implementation(libs.kotlinPoet)
    implementation(libs.kotlinPoet.ksp)
}
