package com.kianmahmoudi.android.shirazgard.activities

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.kianmahmoudi.android.shirazgard.R
import com.kianmahmoudi.android.shirazgard.viewmodel.SettingViewModel
import com.yariksoffice.lingver.Lingver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
abstract class BaseActivity : AppCompatActivity() {

    private val settingViewModel: SettingViewModel by viewModels()

    private var currentLanguage: String = Lingver.getInstance().getLanguage()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeSettings()
    }


    private fun observeSettings() {
        lifecycleScope.launch {
            settingViewModel.languageFlow.collect { lang ->
                if (lang != currentLanguage) {
                    Lingver.getInstance().setLocale(applicationContext, lang)
                    recreate()
                    currentLanguage = lang
                }
            }
        }
        lifecycleScope.launch {
            settingViewModel.themeFlow.collect { theme ->
                updateTheme(theme)
            }
        }
    }

    private fun updateTheme(theme: String) {
        when (theme) {
            "system" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }
}