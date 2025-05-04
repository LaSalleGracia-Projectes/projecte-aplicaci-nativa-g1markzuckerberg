plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.projecte_aplicaci_nativa_g1markzuckerberg"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.projecte_aplicaci_nativa_g1markzuckerberg"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11

    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    //Datastore
    implementation ("androidx.datastore:datastore-preferences:1.1.0")

    //Swipe
    implementation ("androidx.compose.foundation:foundation:1.4.0")

    implementation ("com.airbnb.android:lottie-compose:6.0.0")
    //URL IMAGES
    implementation ("io.coil-kt:coil-compose:2.2.2")

    implementation("com.google.android.gms:play-services-auth:21.1.0")
    implementation("androidx.credentials:credentials:1.3.0-alpha02")
    implementation("androidx.credentials:credentials-play-services-auth:1.3.0-alpha02")

    implementation("io.socket:socket.io-client:2.1.0")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    implementation("com.google.firebase:firebase-messaging:23.1.1")
    implementation(platform("com.google.firebase:firebase-bom:33.13.0"))

    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    implementation (libs.logging.interceptor)
    implementation(libs.androidx.navigation.compose.v276)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.play.services.basement)
    implementation ("androidx.constraintlayout:constraintlayout-compose:1.0.1")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}