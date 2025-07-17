package com.kianmahmoudi.android.shirazgard.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("current.json")
    suspend fun getWeather(
        @Query("key") key: String,
        @Query("q") city: String,
        @Query("aqi") aqi: String = "no",
        @Query("lang") lang: String = "en"
    ): Response<ResponseBody>

    @GET("forecast.json")
    suspend fun getForecast(
        @Query("key") key: String,
        @Query("q") city: String,
        @Query("days") days: Int = 3,
        @Query("aqi") aqi: String = "no",
        @Query("lang") lang: String = "en"
    ): Response<ResponseBody>
}