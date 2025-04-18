package com.kianmahmoudi.android.shirazgard.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kianmahmoudi.android.shirazgard.repository.FavoritePlacesRepository
import com.parse.ParseObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FavoritePlacesViewModel @Inject constructor(
    private val favoritePlacesRepository: FavoritePlacesRepository
) : ViewModel() {

    private val _favoritePlaces = MutableLiveData<List<ParseObject>>()
    val favoritePlaces: LiveData<List<ParseObject>> get() = _favoritePlaces

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> get() = _isFavorite

    fun loadFavoritePlaces() {
        viewModelScope.launch {
            val places = favoritePlacesRepository.getAllFavoritePlaces()
            _favoritePlaces.postValue(places)
        }
    }

    fun checkIfPlaceIsFavorite(userId: String, placeId: String) {
        viewModelScope.launch {
            val favorite = favoritePlacesRepository.isPlaceFavorite(userId, placeId)
            _isFavorite.postValue(favorite)
        }
    }

    fun addPlace(userId: String, placeId: String) {
        viewModelScope.launch {
            favoritePlacesRepository.addFavoritePlace(userId, placeId)
            loadFavoritePlaces()
            _isFavorite.postValue(true)
        }
    }

    fun removePlace(userId: String, placeId: String) {
        viewModelScope.launch{
            favoritePlacesRepository.removeFavoritePlace(userId, placeId)
            loadFavoritePlaces()
            _isFavorite.postValue(false)
        }
    }
}
