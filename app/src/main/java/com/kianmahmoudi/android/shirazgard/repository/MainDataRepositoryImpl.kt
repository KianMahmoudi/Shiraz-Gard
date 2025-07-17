package com.kianmahmoudi.android.shirazgard.repository

import com.kianmahmoudi.android.shirazgard.api.WeatherApi
import com.kianmahmoudi.android.shirazgard.data.WeatherResult
import com.parse.ParseObject
import com.parse.ParseQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class MainDataRepositoryImpl @Inject constructor(
    private val weatherApi: WeatherApi
) : MainDataRepository {

    override suspend fun getPlaceImages(): List<ParseObject> {
        return withContext(Dispatchers.IO) {
            try {
                val query = ParseQuery.getQuery<ParseObject>("photos")
                query.limit = 1000

                suspendCancellableCoroutine { continuation ->
                    query.findInBackground { photoObjects, e ->
                        if (e == null && photoObjects != null) {
                            Timber.tag("ParseRepository").i("Photos fetched: ${photoObjects.size}")
                            continuation.resume(photoObjects)
                        } else {
                            Timber.tag("ParseRepository").e(e, "Error fetching images")
                            continuation.resumeWithException(e ?: Exception("No images found"))
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error in getPlaceImages")
                throw e
            }
        }
    }

    override suspend fun getPlaces(): List<ParseObject> {
        return withContext(Dispatchers.IO) {
            try {
                val query = ParseQuery.getQuery<ParseObject>("Place")
                query.limit = 1000

                suspendCancellableCoroutine { continuation ->
                    query.findInBackground { objects, e ->
                        if (e == null && objects != null) {
                            Timber.tag("ParseRepository").i("Places fetched: ${objects.size}")
                            continuation.resume(objects)
                        } else {
                            Timber.tag("ParseRepository").e(e, "Error fetching places")
                            continuation.resumeWithException(e ?: Exception("No places found"))
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error in getPlaces")
                throw e
            }
        }
    }

    override suspend fun getWeather(): WeatherResult? {
        return withContext(Dispatchers.IO) {
            try {
                val response = weatherApi.getWeather(
                    com.kianmahmoudi.android.shirazgard.BuildConfig.WEATHER_KEY,
                    "Shiraz",
                    "no",
                    "en"
                )

                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        parseWeatherResponse(responseBody.string())
                    } ?: run {
                        Timber.e("Weather response body is null")
                        null
                    }
                } else {
                    Timber.e("Weather API error: ${response.code()} - ${response.message()}")
                    null
                }
            } catch (e: SocketTimeoutException) {
                Timber.e("Weather API timeout: ${e.message}")
                throw e
            } catch (e: UnknownHostException) {
                Timber.e("Weather API host unreachable - VPN may be required: ${e.message}")
                throw e
            } catch (e: Exception) {
                Timber.e("Weather API general error: ${e.message}")
                throw e
            }
        }
    }

    private fun parseWeatherResponse(jsonString: String): WeatherResult? {
        return try {
            val jsonObject = JSONObject(jsonString)
            val current = jsonObject.getJSONObject("current")
            val condition = current.getJSONObject("condition")

            WeatherResult(
                temperature = current.getDouble("temp_c"),
                description = condition.getString("text"),
                icon = condition.getString("icon")
            )
        } catch (e: Exception) {
            Timber.e("Error parsing weather response: ${e.message}")
            null
        }
    }
}