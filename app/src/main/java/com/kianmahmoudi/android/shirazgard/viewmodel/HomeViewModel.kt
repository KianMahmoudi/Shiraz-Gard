package com.kianmahmoudi.android.shirazgard.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kianmahmoudi.android.shirazgard.data.WeatherResult
import com.kianmahmoudi.android.shirazgard.repository.HomeRepository
import kotlinx.coroutines.launch
import com.parse.ParseObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository
) : ViewModel() {

    private val _hotels = MutableLiveData<List<ParseObject>>()
    val hotels: LiveData<List<ParseObject>> get() = _hotels

    private val _restaurants = MutableLiveData<List<ParseObject>>()
    val restaurants: LiveData<List<ParseObject>> get() = _restaurants

    private val _places = MutableLiveData<List<ParseObject>>()
    val places: LiveData<List<ParseObject>> get() = _places

    private val _images = MutableLiveData<List<ParseObject>>()
    val images: LiveData<List<ParseObject>> get() = _images

    private val _weatherData = MutableLiveData<WeatherResult>()
    val weatherData: LiveData<WeatherResult> = _weatherData

    private val _weatherError = MutableLiveData<String>()
    val weatherError: LiveData<String> = _weatherError

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    init {
        fetchAllData()
    }

    fun fetchAllData() {
        _loading.value = true
        viewModelScope.launch {
            try {
                val hotelsDeferred = async { homeRepository.getHotels() }
                val restaurantsDeferred = async { homeRepository.getRestaurants() }
                val weatherDeferred = async { homeRepository.getWeather() }
                val imagesDeferred = async { homeRepository.getPlaceImages() }
                val placesDeferred = async { homeRepository.getPlaces() }

                _hotels.value = hotelsDeferred.await()
                _restaurants.value = restaurantsDeferred.await()
                _images.value = imagesDeferred.await()
                _places.value = placesDeferred.await()

                weatherDeferred.await()?.let {
                    _weatherData.postValue(it)
                } ?: run {
                    _weatherError.postValue("No weather data found")
                }
            } catch (e: Exception) {
                _weatherError.postValue(e.localizedMessage)
                Log.e("HomeViewModel", "Error fetching data", e)
            } finally {
                _loading.value = false
            }
        }
    }
}