plugins {
    alias(libs.plugins.android.application)
    id ("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

// values fetched from gradle.properties (where you hide your API keys)
val spotifyClientId   = project.property("SPOTIFY_CLIENT_ID")   as String
val spotifyClientSecret = project.property("SPOTIFY_CLIENT_SECRET") as String

android {
    namespace = "com.example.vibeifyfer"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.vibeifyfer"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "SPOTIFY_CLIENT_ID",
            "\"$spotifyClientId\""
        )
        buildConfigField(
            "String",
            "SPOTIFY_CLIENT_SECRET",
            "\"$spotifyClientSecret\""
        )
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
}

// CameraX
val cameraxVersion = "1.3.1"

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.google.android.material:material:1.9.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation ("androidx.core:core-ktx:1.13.1")
    implementation ("androidx.activity:activity-ktx:1.8.2")
    implementation ("androidx.fragment:fragment-ktx:1.6.2")
    implementation(libs.firebase.common)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)

    // Crashlytics runtime SDK only
    implementation("com.google.firebase:firebase-crashlytics:18.4.1")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)


    // ML Kit Face Detection
    implementation("com.google.mlkit:face-detection:16.1.5")

    // CameraX dependencies
    implementation("androidx.camera:camera-core:$cameraxVersion")
    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation("androidx.camera:camera-view:$cameraxVersion")

    // Glide for image loading
    implementation("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")

    // ZXing QR Generator library
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")

    // Guava standard library for ListenableFuture
    implementation("com.google.guava:guava:31.1-android")
    
}