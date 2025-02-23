import java.net.URI

// Top-level build file where you can add configuration options common to all sub-projects/modules.
@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.maps.secret)
    id("androidx.navigation.safeargs.kotlin") version "2.7.6" apply false
    id("com.google.dagger.hilt.android") version "2.51.1" apply false
    id("com.google.devtools.ksp") version "2.1.10-1.0.30" apply false
}

true // Needed to make the Suppress annotation work for the wplugins block