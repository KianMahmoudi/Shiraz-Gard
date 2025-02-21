package com.kianmahmoudi.android.shirazgard.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kianmahmoudi.android.shirazgard.data.WeatherResult
import com.kianmahmoudi.android.shirazgard.repository.MainDataRepository
import kotlinx.coroutines.launch
import com.parse.ParseObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainDataViewModel @Inject constructor(
    private val mainDataRepository: MainDataRepository
) : ViewModel() {

    private val _places = MutableLiveData<List<ParseObject>>()
    val places: LiveData<List<ParseObject>> get() = _places

    private val _images = MutableLiveData<List<ParseObject>>()
    val images: LiveData<List<ParseObject>> get() = _images

    private val _weatherData = MutableLiveData<WeatherResult>()
    val weatherData: LiveData<WeatherResult> = _weatherData

    private val _weatherError = MutableLiveData<String>()
    val weatherError: LiveData<String> = _weatherError

    init {
        fetchAllData()
    }

    fun fetchAllData() {
        viewModelScope.launch {
            try {
                val weatherDeferred = async { mainDataRepository.getWeather() }
                val imagesDeferred = async { mainDataRepository.getPlaceImages() }
                val placesDeferred = async { mainDataRepository.getPlaces() }

                _images.value = imagesDeferred.await()
                _places.value = placesDeferred.await()

                weatherDeferred.await()?.let {
                    _weatherData.postValue(it)
                } ?: run {
                    _weatherError.postValue("No weather data found")
                }
            } catch (e: Exception) {
                _weatherError.postValue(e.localizedMessage)
                Timber.tag("HomeViewModel").e(e, "Error fetching data")
            }
        }
    }
}