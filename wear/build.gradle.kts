plugins {
    id("com.android.application")
    id("com.android.built-in-kotlin")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.yaleiden.nwsdata.wear"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.yaleiden.nwsdata"
        minSdk = 30
        targetSdk = 36
        versionCode = 1000028
        versionName = "1.0.1"

    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    useLibrary("wear-sdk")
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2025.12.00"))
    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.core:core-splashscreen:1.2.0")
    implementation("androidx.wear.compose:compose-foundation:1.5.6")
    implementation("androidx.wear.compose:compose-material3:1.5.6")
    implementation("androidx.wear.compose:compose-ui-tooling:1.5.6")
    implementation("androidx.wear.protolayout:protolayout-material3:1.3.0")
    implementation("androidx.wear.protolayout:protolayout:1.3.0")
    implementation("androidx.wear.tiles:tiles-tooling-preview:1.5.0")
    implementation("androidx.wear.tiles:tiles:1.5.0")
    implementation("androidx.wear.watchface:watchface-complications-data-source-ktx:1.2.1")
    implementation("androidx.wear:wear-tooling-preview:1.0.0")
    implementation("com.google.android.gms:play-services-wearable:20.0.1")
    implementation("com.google.guava:guava:33.2.1-android")
    androidTestImplementation(platform("androidx.compose:compose-bom:2025.12.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.wear.tiles:tiles-renderer:1.5.0")
    debugImplementation("androidx.wear.tiles:tiles-tooling:1.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-guava:1.7.3")
}