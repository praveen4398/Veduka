plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services") // ✅ Google Services plugin
}

android {
    namespace = "com.example.vedukamad"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.vedukamad"
        minSdk = 25
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    implementation(libs.firebase.auth.v2231)
    implementation(libs.play.services.auth)
    implementation (libs.places)
    implementation(libs.glide)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
    annotationProcessor(libs.compiler)
    implementation(libs.picasso)
    implementation(libs.play.services.maps)
    implementation(libs.material.v1110)
    implementation (libs.play.services.location)
    implementation ("com.kizitonwose.calendar:view:2.3.0")


    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
