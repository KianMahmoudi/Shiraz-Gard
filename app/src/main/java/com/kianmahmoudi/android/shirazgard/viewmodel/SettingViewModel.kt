package com.kianmahmoudi.android.shirazgard.viewmodel

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {



    companion object {
        val LANGUAGE_KEY = stringPreferencesKey("lang")
        val THEME_KEY = stringPreferencesKey("theme")
    }


    val languageFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[LANGUAGE_KEY] ?: "en"
    }


    val themeFlow: Flow<String> = dataStore.data.map { preferences ->
            preferences[THEME_KEY] ?: "system"
        }


    fun saveLanguage(language: String) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[LANGUAGE_KEY] = language
            }
        }
    }

    fun saveTheme(theme: String) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[THEME_KEY] = theme
            }
        }
    }

}