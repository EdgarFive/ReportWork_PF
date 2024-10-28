plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.reportwork"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.reportwork"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(libs.gms.maps)       // Agregando Google Maps
    implementation(libs.gms.location)   // Agregando Google Location

    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("androidx.fragment:fragment:1.4.1")


}