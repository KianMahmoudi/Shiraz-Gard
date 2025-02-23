package com.kianmahmoudi.android.shirazgard.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.parse.ParseObject
import com.parse.ParseQuery
import javax.inject.Inject

class FavoritePlacesRepositoryImpl @Inject constructor() :
    FavoritePlacesRepository {

    override fun getAllFavoritePlaces(): List<ParseObject> {
        return try {
            val query = ParseQuery.getQuery<ParseObject>("FavoritePlaces")
            query.find()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun isPlaceFavorite(userId: String, placeId: String): Boolean {
        return try {
            val query = ParseQuery.getQuery<ParseObject>("FavoritePlaces")
            query.whereEqualTo("userId", userId)
            query.whereEqualTo("placeId", placeId)

            val results = query.find()
            results.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }


    override suspend fun addFavoritePlace(userId: String, placeId: String) {
        val favoritePlace = ParseObject("FavoritePlaces")
        favoritePlace.put("userId", userId)
        favoritePlace.put("placeId", placeId)
        favoritePlace.saveInBackground { e ->
            if (e == null) {

            } else {

            }
        }
    }

    override suspend fun removeFavoritePlace(userId: String, placeId: String) {
        val query = ParseQuery.getQuery<ParseObject>("FavoritePlaces")
        query.whereEqualTo("userId", userId)
        query.whereEqualTo("placeId", placeId)

        query.findInBackground { results, e ->
            if (e == null) {
                results?.forEach { it.deleteInBackground() }
            } else {

            }
        }
    }

}