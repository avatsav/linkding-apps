plugins { id("convention.kotlin.multiplatform") }

kotlin { sourceSets { commonMain.dependencies { api(libs.kotlin.datetime) } } }
