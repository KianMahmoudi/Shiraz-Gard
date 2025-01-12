package com.kianmahmoudi.android.shirazgard.repository

import com.kianmahmoudi.android.shirazgard.data.WeatherResult
import com.parse.ParseObject

interface HomeRepository {
    suspend fun getPlaceImages(): List<ParseObject>
    suspend fun getHotels(): List<ParseObject>
    suspend fun getRestaurants() :List<ParseObject>
    suspend fun getWeather(): WeatherResult?

    suspend fun getPlaces(): List<ParseObject>
}