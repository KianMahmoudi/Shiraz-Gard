package com.kianmahmoudi.android.shirazgard.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kianmahmoudi.android.shirazgard.data.WeatherResult
import com.kianmahmoudi.android.shirazgard.repository.HomeRepository
import kotlinx.coroutines.launch
import com.parse.ParseObject
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val homeRepository: HomeRepository) :
    ViewModel() {

    private val _hotels = MutableLiveData<List<ParseObject>>()
    val hotels: LiveData<List<ParseObject>> get() = _hotels

    private val _restaurants = MutableLiveData<List<ParseObject>>()
    val restaurants: LiveData<List<ParseObject>> get() = _restaurants

    private val _images = MutableLiveData<List<ParseObject>>()
    val images: LiveData<List<ParseObject>> get() = _images

    private val _weatherData = MutableLiveData<WeatherResult>()
    val weatherData: LiveData<WeatherResult> = _weatherData

    private val _weatherError = MutableLiveData<String>()
    val weatherError: LiveData<String> = _weatherError

    init {
        fetchHotels()
        fetchRestaurants()
        fetchWeather()
        fetchImages()
    }

    private fun fetchHotels() {
        viewModelScope.launch {
            try {
                val hotelList = homeRepository.getHotels()
                _hotels.value = hotelList
            } catch (e: Exception) {

            }
        }
    }

    private fun fetchRestaurants() {
        viewModelScope.launch {
            try {
                val restaurantsList = homeRepository.getRestaurants()
                _restaurants.value = restaurantsList
            } catch (e: Exception) {

            }
        }
    }

    private fun fetchWeather() {
        viewModelScope.launch {
            try {
                val result = homeRepository.getWeather()
                result?.let {
                    _weatherData.postValue(it)
                } ?: run {
                    _weatherError.postValue("No weather data found")
                }
            } catch (e: Exception) {
                _weatherError.postValue(e.message)
            }
        }
    }

    private fun fetchImages() {
        viewModelScope.launch {
            try {
                val imageList = homeRepository.getPlaceImages()
                _images.value = imageList
            } catch (e: Exception) {

            }
        }
    }

}