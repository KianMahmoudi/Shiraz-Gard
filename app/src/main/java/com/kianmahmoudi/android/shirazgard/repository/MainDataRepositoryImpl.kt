package com.kianmahmoudi.android.shirazgard.repository

import com.kianmahmoudi.android.shirazgard.api.RetrofitInstance
import com.kianmahmoudi.android.shirazgard.data.WeatherResult
import com.parse.ParseObject
import com.parse.ParseQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class MainDataRepositoryImpl : MainDataRepository {
    override suspend fun getPlaceImages(): List<ParseObject> {
        val query = ParseQuery.getQuery<ParseObject>("photos")
        return withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { continuation ->
                query.findInBackground { photoObjects, e ->
                    if (e == null && photoObjects != null) {
                        Timber.tag("ParseHotelRepository").i("Photos: $photoObjects")
                        continuation.resume(photoObjects)
                    } else {
                        Timber.tag("ParseHotelRepository").e(e, "Error fetching images")
                        continuation.resumeWithException(e ?: Exception("No results found"))
                    }
                }
            }
        }
    }

    override suspend fun getWeather(): WeatherResult? {
        val response = RetrofitInstance.api.getWeather(
            "شیراز",
            com.kianmahmoudi.android.shirazgard.BuildConfig.WEATHER_KEY
        )

        if (response.isSuccessful) {
            response.body()?.let {
                try {
                    val jsonObject = JSONObject(it.string())

                    if (jsonObject.has("result")) {
                        val result = jsonObject.getJSONObject("result")

                        val main = result.getJSONObject("main")
                        val temperature = main.getDouble("temp")

                        val weatherArray = result.getJSONArray("weather")
                        val weatherDescription =
                            weatherArray.getJSONObject(0).getString("description")
                        val weatherIc = weatherArray.getJSONObject(0).getString("icon")

                        return WeatherResult(temperature, weatherDescription, weatherIc)
                    } else {
                        Timber.tag("WeatherRepository").e("No 'result' field in JSON")
                    }
                } catch (e: Exception) {
                    Timber.tag("WeatherRepository").e("Error parsing JSON: %s", e.message)
                    return null
                }
            }
        } else {
            Timber.tag("WeatherRepository").e("Response failed: %s", response.code())
        }

        return null
    }

    override suspend fun getPlaces(): List<ParseObject> {
        val query = ParseQuery.getQuery<ParseObject>("Place")
        return withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { continuation ->
                query.findInBackground { objects, e ->
                    if (e == null) {
                        continuation.resume(objects)
                    } else {
                        Timber.tag("HomeFragment").i(e.message.toString())
                        continuation.resumeWithException(e)
                    }
                }
            }
        }
    }

}