package com.kianmahmoudi.android.shirazgard.repository

import android.util.Log
import com.kianmahmoudi.android.shirazgard.api.RetrofitInstance
import com.kianmahmoudi.android.shirazgard.data.WeatherResult
import com.parse.ParseObject
import com.parse.ParseQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class ParseHomeRepository : HomeRepository {
    override suspend fun getPlaceImages(placeId: String): List<String> {
        val query = ParseQuery.getQuery<ParseObject>("photos")
        query.whereEqualTo("placeId", placeId)
        return withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { continuation ->
                query.getFirstInBackground { photoObject, e ->
                    if (e == null && photoObject != null) {
                        val photoUrls = photoObject.getList<String>("photosUrl") ?: emptyList()
                        Log.i("ParseHotelRepository", "Photos: $photoUrls")
                        continuation.resume(photoUrls)
                    } else {
                        Log.e("ParseHotelRepository", "Error fetching images", e)
                        continuation.resumeWithException(e ?: Exception("No results found"))
                    }
                }
            }
        }
    }

    override suspend fun getHotels(): List<ParseObject> {
        val query = ParseQuery.getQuery<ParseObject>("Place")
        query.whereEqualTo("type", "hotel")
        return withContext(Dispatchers.IO) {
            suspendCancellableCoroutine{ continuation ->
                query.findInBackground { objects, e ->
                    if (e == null) {
                        continuation.resume(objects)
                    } else {
                        Log.i("HomeFragment", e.message.toString())
                        continuation.resumeWithException(e)
                    }
                }
            }
        }
    }

    override suspend fun getRestaurants(): List<ParseObject> {
        val query = ParseQuery.getQuery<ParseObject>("Place")
        query.whereEqualTo("type", "restaurant")
        return withContext(Dispatchers.IO) {
            suspendCancellableCoroutine{ continuation ->
                query.findInBackground{ objects, e ->
                    if (e == null) {
                        continuation.resume(objects)
                    } else {
                        Log.i("HomeFragment", e.message.toString())
                        continuation.resumeWithException(e)
                    }
                }
            }
        }
    }

    override suspend fun getWeather(): WeatherResult? {
        val response = RetrofitInstance.api.getWeather("شیراز", "mfggRHJXqMCI1yyQxPEsByuQzulsskdrAnESXdpnXu1Bq3iWtUai")

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