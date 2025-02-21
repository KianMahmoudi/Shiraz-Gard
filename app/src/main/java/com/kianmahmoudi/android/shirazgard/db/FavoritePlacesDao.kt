package com.kianmahmoudi.android.shirazgard.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kianmahmoudi.android.shirazgard.data.Place

@Dao
interface FavoritePlacesDao {
    @Query("SELECT * FROM place")
    fun getAllFavoritePlaces(): LiveData<List<Place>>

    @Insert
    suspend fun insertFavoritePlace(place: Place)

    @Delete
    suspend fun deleteFavoritePlace(place: Place)

    @Query("UPDATE Place SET isFavorite = :isFavorite WHERE objectId = :placeId")
    suspend fun updatePlace(placeId: String, isFavorite: Boolean)

    @Query("SELECT * FROM place WHERE objectId = :objectId") // Add this query
    fun getPlaceById(objectId: String): LiveData<Place?>

}