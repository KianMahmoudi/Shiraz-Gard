import java.util.Properties

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.maps.secret)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("androidx.navigation.safeargs.kotlin") }

android {
    namespace = "com.kianmahmoudi.android.shirazgard"
    compileSdk = 34

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }


    defaultConfig {
        manifestPlaceholders += mapOf()
        applicationId = "com.kianmahmoudi.android.shirazgard"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        manifestPlaceholders["MAPS_KEY"] = (project.properties["MAPS_KEY"] as? String ?: "")
        manifestPlaceholders["BACK4APP_SERVER_URL"] = (project.properties["BACK4APP_SERVER_URL"] as? String ?: "")
        manifestPlaceholders["BACK4APP_APP_ID"] = (project.properties["BACK4APP_APP_ID"] as? String ?: "")
        manifestPlaceholders["BACK4APP_CLIENT_KEY"] = (project.properties["BACK4APP_CLIENT_KEY"] as? String ?: "")
        buildConfigField(
        "String",
        "MAPS_KEY",
        "\"${project.findProperty("MAPS_KEY")}\""
    )
        buildConfigField("String", "WEATHER_KEY", "\"${project.findProperty("WEATHER_KEY")}\"")
        buildConfigField(
            "String",
            "BACK4APP_SERVER_URL",
            "\"${project.findProperty("BACK4APP_SERVER_URL")}\""
        )
        buildConfigField(
            "String",
            "BACK4APP_CLIENT_KEY",
            "\"${project.findProperty("BACK4APP_CLIENT_KEY")}\""
        )
        buildConfigField(
            "String",
            "BACK4APP_APP_ID",
            "\"${project.findProperty("BACK4APP_APP_ID")}\""
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    val nav_version = "2.8.1"
    val room_version = "2.6.1"

    implementation(libs.annotations)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.google.maps)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
    implementation(libs.parse)
    implementation(libs.hilt.android)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.glide)
    implementation(platform(libs.kotlin.bom))
    implementation(libs.lottie)
    implementation(libs.imageslideshow)
    implementation(libs.logger)
    implementation(libs.timber)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.lingver)
    implementation(libs.process.phoenix)
    ksp(libs.hilt.android.compiler)
    ksp(libs.androidx.room.compiler)
}