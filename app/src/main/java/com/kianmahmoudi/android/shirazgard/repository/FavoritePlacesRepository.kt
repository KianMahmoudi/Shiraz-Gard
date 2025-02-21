package com.kianmahmoudi.android.shirazgard.repository

import androidx.lifecycle.LiveData
import com.kianmahmoudi.android.shirazgard.data.Place

interface FavoritePlacesRepository {
    fun getAllFavoritePlaces(): LiveData<List<Place>>
    suspend fun addFavoritePlace(place: Place)
    suspend fun removeFavoritePlace(place: Place)
    suspend fun updatePlace(id:String,isFavorite:Boolean)
    fun getFavoritePlaces(): LiveData<List<Place>>
    fun getPlaceById(objectId: String): LiveData<Place?>
}