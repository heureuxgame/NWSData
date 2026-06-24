// WEAR MODULE
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.android.builtInKotlin)
    alias(libs.plugins.kotlin.compose)
}

// ✅ Explicitly targets the modern AGP 9.x/10.x API extension
configure<com.android.build.api.dsl.ApplicationExtension> {
    namespace = "com.yaleiden.nwsdata.wear"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.yaleiden.nwsdata"
        minSdk = 30
        targetSdk = 36
        versionCode = 1000029
        versionName = "1.0.2"

        // 1️⃣ GRAB THE KEY LOCALLY INSIDE THE WEAR MODULE
        //val apiKey = rootProject.findProperty("WEATHER_API_KEY")?.toString() ?: ""

        // 2️⃣ INJECT IT INTO WEAR'S BUILDCONFIG
        //buildConfigField("String", "WEATHER_API_KEY", "\"$apiKey\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // ✅ FIXED: Modern, type-safe API for platform SDK libraries (No warning!)
    useLibrary("wear-sdk")

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    // Jetpack Compose BOM
    implementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(platform(libs.androidx.compose.bom))

    // Core Android / UI / Components
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.splashscreen)

    // Wear OS Compose
    implementation(libs.androidx.wear.compose.foundation)
    implementation(libs.compose.material3)
    implementation(libs.androidx.wear.compose.ui.tooling)
    implementation(libs.wear.tooling.preview)

    // Wear Protolayout & Tiles
    implementation(libs.androidx.wear.protolayout)
    implementation(libs.androidx.wear.protolayout.material3)
    implementation(libs.tiles)
    implementation(libs.tiles.tooling.preview)

    // Watchfaces & Play Services
    implementation(libs.watchface.complications.data.source.ktx)
    implementation(libs.play.services.wearable)

    // Concurrency & Collections Utilites
    implementation(libs.guava)
    implementation(libs.kotlinx.coroutines.guava)

    // Development & Testing Implementations
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.tiles.renderer)
    debugImplementation(libs.tiles.tooling)

    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}