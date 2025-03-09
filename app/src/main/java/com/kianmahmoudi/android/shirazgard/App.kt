package com.kianmahmoudi.android.shirazgard


import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatDelegate
import com.kianmahmoudi.android.shirazgard.viewmodel.SettingViewModel
import com.parse.Parse
import com.yariksoffice.lingver.Lingver
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Parse.initialize(
            Parse.Configuration.Builder(this)
                .applicationId(BuildConfig.BACK4APP_APP_ID)
                .clientKey(BuildConfig.BACK4APP_CLIENT_KEY)
                .server(BuildConfig.BACK4APP_SERVER_URL)
                .enableLocalDataStore()
                .build()
        )
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        Lingver.init(this, "en")

    }
}