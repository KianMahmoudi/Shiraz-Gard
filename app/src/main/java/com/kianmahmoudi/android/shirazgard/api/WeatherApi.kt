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
        @Query("aqi") aqi: String = "no"
    ): Response<ResponseBody>

}