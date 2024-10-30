package com.kianmahmoudi.android.shirazgard.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("weather/")
    suspend fun getWeather(
        @Query("city") city: String,
        @Query("license") licence: String
    ): Response<ResponseBody>

}