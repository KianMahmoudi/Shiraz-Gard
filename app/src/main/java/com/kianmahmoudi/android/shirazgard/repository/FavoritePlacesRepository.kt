package com.kianmahmoudi.android.shirazgard.repository

import androidx.lifecycle.LiveData
import com.parse.ParseObject

interface FavoritePlacesRepository {
    fun getAllFavoritePlaces(): List<ParseObject>
    fun isPlaceFavorite(userId: String, placeId: String): Boolean
    suspend fun addFavoritePlace(userId: String, placeId: String)
    suspend fun removeFavoritePlace(userId: String, placeId: String)
}