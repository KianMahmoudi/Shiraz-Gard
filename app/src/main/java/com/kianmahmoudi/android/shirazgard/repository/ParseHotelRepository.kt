package com.kianmahmoudi.android.shirazgard.repository

import android.util.Log
import com.parse.ParseObject
import com.parse.ParseQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class ParseHotelRepository : HotelRepository {
    override suspend fun getHotelImages(hotelId: String): List<String> {
        val query = ParseQuery.getQuery<ParseObject>("photos")
        query.whereEqualTo("placeId", hotelId)
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
}