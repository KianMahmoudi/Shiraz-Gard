package com.kianmahmoudi.android.shirazgard.repository

import com.kianmahmoudi.android.shirazgard.data.WeatherResult
import com.parse.ParseObject

interface MainDataRepository {
    suspend fun getPlaceImages(): List<ParseObject>
    suspend fun getWeather(): WeatherResult?
    suspend fun getPlaces(): List<ParseObject>
}