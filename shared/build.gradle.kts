import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.kotlin.plugin.serialization)
}

kotlin {

    @OptIn(ExperimentalWasmDsl::class) wasmJs {
        moduleName = "composeApp"
        browser()
    }

    jvm()

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.ktor.serialization.kotlinx.json)
            }
        }
    }

    androidLibrary {
        namespace = "pl.cube.shared"
        compileSdk = 35
    }
}