package com.kianmahmoudi.android.shirazgard.repository

import com.parse.ParseObject
import org.json.JSONArray

interface HotelRepository {
    suspend fun getHotelImages(hotelId: String): List<String>
    suspend fun getHotels(): List<ParseObject>
}