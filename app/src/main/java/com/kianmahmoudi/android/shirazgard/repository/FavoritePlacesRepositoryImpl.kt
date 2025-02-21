package com.kianmahmoudi.android.shirazgard.repository

import androidx.lifecycle.LiveData
import com.kianmahmoudi.android.shirazgard.data.Place
import com.kianmahmoudi.android.shirazgard.db.FavoritePlacesDao
import javax.inject.Inject

class FavoritePlacesRepositoryImpl @Inject constructor(private val favoritePlacesDao: FavoritePlacesDao) :
    FavoritePlacesRepository {

    override fun getAllFavoritePlaces(): LiveData<List<Place>> {
        return favoritePlacesDao.getAllFavoritePlaces()
    }

    override suspend fun addFavoritePlace(place: Place) {
        favoritePlacesDao.insertFavoritePlace(place)
    }

    override suspend fun removeFavoritePlace(place: Place) {
        favoritePlacesDao.deleteFavoritePlace(place)
    }

    override suspend fun updatePlace(id: String, isFavorite: Boolean) {
        favoritePlacesDao.updatePlace(id, isFavorite)
    }

    override fun getFavoritePlaces(): LiveData<List<Place>> {
        return favoritePlacesDao.getAllFavoritePlaces()
    }

    override fun getPlaceById(objectId: String): LiveData<Place?> { // Implement this function
        return favoritePlacesDao.getPlaceById(objectId)
    }

}