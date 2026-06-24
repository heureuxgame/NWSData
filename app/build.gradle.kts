// APP MODULE

plugins {
    alias(libs.plugins.android.application)
    //alias(libs.plugins.kotlin.android) // Keep this active!
}

configure<com.android.build.api.dsl.ApplicationExtension> {
    namespace = "com.yaleiden.nwsdata"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.yaleiden.nwsdata"
        minSdk = 28
        targetSdk = 36
        versionCode = 27
        versionName = "1.27"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val apiKey = project.findProperty("WEATHER_API_KEY")?.toString() ?: ""
        buildConfigField("String", "WEATHER_API_KEY", "\"$apiKey\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
} // End of ApplicationExtension block

// FIX: Declare the Kotlin extensions using explicit scoping outside the block
kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    // ... your unchanged dependencies block stays down here
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.google.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.scalars)
    implementation(libs.google.gson)
    testImplementation(libs.test.junit)
    androidTestImplementation(libs.androidTest.ext.junit)
    androidTestImplementation(libs.androidTest.espresso)
}