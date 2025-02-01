package com.kianmahmoudi.android.shirazgard


import android.app.Application
import com.parse.Parse
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Parse.initialize(
            Parse.Configuration.Builder(this)
                .applicationId(BuildConfig.BACK4APP_APP_ID)
                .clientKey(BuildConfig.BACK4APP_CLIENT_KEY)
                .server(BuildConfig.BACK4APP_SERVER_URL)
                .build()
        );
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}