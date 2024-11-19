/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    id("convention.android.library")
    id("convention.kotlin.multiplatform")
}

android {
    namespace = "androidx.paging.common"
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(libs.kotlin.coroutines.core)
                api(libs.androidx.annotations)
            }
        }

        jvmMain {
            dependencies {
                api(libs.androidx.archCore)
            }
        }

        androidMain {
            dependencies {
                api(libs.androidx.archCore)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.coroutines.test)
                implementation(libs.kotlin.test)

            }
        }

        nativeMain {
            dependencies {
                implementation(libs.kotlin.atomicfu)
            }
        }

//
//        commonJvmAndroidTest {
//            dependsOn(commonTest)
//            dependencies {
//                implementation(libs.junit)
//                implementation(libs.mockitoCore)
//                implementation(libs.mockitoKotlin)
//                implementation(project(":internal-testutils-common"))
//                implementation(project(":internal-testutils-ktx"))
//            }
//        }
//
//        jvmTest {
//            dependsOn(commonJvmAndroidTest)
//        }
//
//        androidInstrumentedTest {
//            dependsOn(commonJvmAndroidTest)
//            dependencies {
//                implementation(libs.testRunner)
//                implementation(libs.mockitoAndroid5)
//            }
//        }
//        nativeTest {
//            dependsOn(commonTest)
//        }
//
//        darwinMain {
//            dependsOn(nativeMain)
//        }
//
//        linuxMain {
//            dependsOn(nativeMain)
//        }
    }
}
