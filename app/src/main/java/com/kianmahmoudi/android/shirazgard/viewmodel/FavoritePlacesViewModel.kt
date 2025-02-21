package com.kianmahmoudi.android.shirazgard.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kianmahmoudi.android.shirazgard.data.Place
import com.kianmahmoudi.android.shirazgard.repository.FavoritePlacesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FavoritePlacesViewModel @Inject constructor(private val favoritePlacesRepository: FavoritePlacesRepository) :
    ViewModel() {

    val favoritePlaces: LiveData<List<Place>> = favoritePlacesRepository.getAllFavoritePlaces()

    fun addFavoritePlace(place: Place) {
        viewModelScope.launch {
            favoritePlacesRepository.addFavoritePlace(place)
        }
    }

    fun removeFavoritePlace(place: Place) {
        viewModelScope.launch {
            favoritePlacesRepository.removeFavoritePlace(place)
        }
    }

    fun setFavorite(id: String, isFavorite: Boolean) {
        viewModelScope.launch {
            favoritePlacesRepository.updatePlace(id, isFavorite)
        }
    }

    fun getPlaceById(objectId: String): LiveData<Place?> { // Add this function
        return favoritePlacesRepository.getPlaceById(objectId)
    }

}