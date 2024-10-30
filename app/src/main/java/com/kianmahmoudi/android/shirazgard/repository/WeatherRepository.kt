package com.kianmahmoudi.android.shirazgard.repository

import android.util.Log
import com.kianmahmoudi.android.shirazgard.api.RetrofitInstance
import com.kianmahmoudi.android.shirazgard.api.WeatherApi
import com.kianmahmoudi.android.shirazgard.data.WeatherResult
import org.json.JSONObject
import javax.inject.Inject


class WeatherRepository @Inject constructor(
    private val weatherApi: WeatherApi
) {


    suspend fun getWeather(city: String, licence: String): WeatherResult? {
        val response = RetrofitInstance.api.getWeather(city, licence)

        if (response.isSuccessful) {
            response.body()?.let {
                try {
                    val jsonObject = JSONObject(it.string())

                    if (jsonObject.has("result")) {
                        val result = jsonObject.getJSONObject("result")

                        val main = result.getJSONObject("main")
                        val temperature = main.getDouble("temp")

                        val weatherArray = result.getJSONArray("weather")
                        val weatherDescription = weatherArray.getJSONObject(0).getString("description")
                        val weatherIc = weatherArray.getJSONObject(0).getString("icon")

                        return WeatherResult(temperature, weatherDescription,weatherIc)
                    } else {
                        Log.e("WeatherRepository", "No 'result' field in JSON")
                    }
                } catch (e: Exception) {
                    Log.e("WeatherRepository", "Error parsing JSON: ${e.message}")
                    return null
                }
            }
        } else {
            Log.e("WeatherRepository", "Response failed: ${response.code()}")
        }

        return null
    }


}
