plugins {
    AndroidPluginDependencies.plugins.forEach { (lib, v) ->
        if (v.isNotBlank()) {
            id(lib) version v
        } else {
            id(lib)
        }
    }
}

android {
    namespace = "com.mutualmobile.harvestKmp.android"
    compileSdk = 33
    defaultConfig {
        applicationId = "com.mutualmobile.harvestKmp.android"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        vectorDrawables {
            useSupportLibrary = true
        }
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
        kotlin {
            kotlinOptions {
                freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
            }
        }
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = AndroidDependencyVersions.composeKotlinCompiler
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    lint {
          checkReleaseBuilds = false
          abortOnError = false
          xmlReport = false
          htmlReport = false
    }
}

dependencies {
    implementation(project(":shared"))
    implementation("androidx.activity:activity-ktx:1.6.1")

    // Firebase Cloud Messaging
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.accompanist:accompanist-permissions:0.25.1")
    // QR
    implementation("com.google.zxing:core:3.4.1")
    implementation("com.journeyapps:zxing-android-embedded:4.1.0")
    // BIOMETRIC
    implementation("androidx.biometric:biometric:1.0.0")
    //
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.compose.material3:material3:1.0.0-alpha02")
    implementation("androidx.compose.material:material-icons-extended:1.0.1")
    implementation("com.airbnb.android:lottie-compose:4.2.0")

    AndroidDependencies.platforms.forEach { platformDependency ->
        implementation(platform(platformDependency))
    }
    AndroidDependencies.implementation.forEach(::implementation)
    AndroidDependencies.androidTestImplementation.forEach(::androidTestImplementation)
    AndroidDependencies.debugImplementation.forEach(::debugImplementation)
}