plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization") version libs.versions.serialization
    alias(libs.plugins.devtools.ksp)
    alias(libs.plugins.hilt.android)
    id("org.jetbrains.kotlin.plugin.parcelize")
}

android {
    namespace = "com.migvidal.materialwiki"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.migvidal.materialwiki"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }

    val versionDimension = "version"

    flavorDimensions += versionDimension

    productFlavors {
        create("prod") {
            dimension = versionDimension
        }
        create("mocked") {
            dimension = versionDimension
            applicationIdSuffix = ".mocked"
            versionNameSuffix = "-mocked"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)
    implementation(libs.coil.network.okhttp)
    implementation(libs.coil.svg)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp.jvm)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.okhttp)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.bundles.compose.adaptive)

}