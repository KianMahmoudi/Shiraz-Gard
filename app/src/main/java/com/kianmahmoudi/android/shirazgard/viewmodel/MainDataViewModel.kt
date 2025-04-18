package com.kianmahmoudi.android.shirazgard.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kianmahmoudi.android.shirazgard.data.WeatherResult
import com.kianmahmoudi.android.shirazgard.repository.MainDataRepository
import com.kianmahmoudi.android.shirazgard.util.NetworkUtils
import com.parse.Parse.getApplicationContext
import kotlinx.coroutines.launch
import com.parse.ParseObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import timber.log.Timber
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
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

    private val _dataLoadingError = MutableLiveData<String>()
    val dataLoadingError: LiveData<String> = _dataLoadingError


    fun fetchAllData() {
        viewModelScope.launch {
            try {
                if (!NetworkUtils.isOnline(getApplicationContext())) {
                    _dataLoadingError.postValue("No internet connection.")
                    return@launch
                }

                val imagesDeferred = async { mainDataRepository.getPlaceImages() }
                val placesDeferred = async { mainDataRepository.getPlaces() }
                val weatherDeferred = async { mainDataRepository.getWeather() }

                try {
                    if (!NetworkUtils.isOnline(getApplicationContext())) {
                        _dataLoadingError.postValue("Internet connection lost.")
                        return@launch
                    }
                    _images.value = imagesDeferred.await()
                } catch (e: SocketTimeoutException) {
                    Timber.e(e, "Timeout fetching images")
                    _dataLoadingError.postValue("Timeout fetching images. Please try again.")
                } catch (e: IOException) {
                    Timber.e(e, "IOError fetching images")
                    _dataLoadingError.postValue("Network error fetching images. Please check your connection.")
                } catch (e: Exception) {
                    Timber.e(e, "Error fetching images")
                    _dataLoadingError.postValue("Error fetching images: ${e.localizedMessage}")
                }

                try {
                    if (!NetworkUtils.isOnline(getApplicationContext())) {
                        _dataLoadingError.postValue("Internet connection lost.")
                        return@launch
                    }
                    _places.value = placesDeferred.await()
                } catch (e: SocketTimeoutException) {
                    Timber.e(e, "Timeout fetching places")
                    _dataLoadingError.postValue("Timeout fetching places. Please try again.")
                } catch (e: IOException) {
                    Timber.e(e, "IOError fetching places")
                    _dataLoadingError.postValue("Network error fetching places. Please check your connection.")
                } catch (e: Exception) {
                    Timber.e(e, "Error fetching places")
                    _dataLoadingError.postValue("Error fetching places: ${e.localizedMessage}")
                }

                try {
                    if (!NetworkUtils.isOnline(getApplicationContext())) {
                        _weatherError.postValue("Internet connection lost.")
                        return@launch
                    }
                    weatherDeferred.await()?.let {
                        _weatherData.postValue(it)
                    } ?: run {
                        _weatherError.postValue("No weather data found")
                    }
                } catch (e: UnknownHostException) {
                    Timber.e(e, "UnknownHostException fetching weather")
                    _weatherError.postValue("Problem connecting to weather server. Please check your internet connection.")
                } catch (e: SocketTimeoutException) {
                    Timber.e(e, "Timeout fetching weather")
                    _weatherError.postValue("Timeout fetching weather. Please try again.")
                } catch (e: IOException) {
                    Timber.e(e, "IOError fetching weather")
                    _weatherError.postValue("Network error fetching weather. Please check your connection.")
                } catch (e: Exception) {
                    Timber.e(e, "Error fetching weather")
                    _weatherError.postValue(e.localizedMessage)
                }

            } catch (e: Exception) {
                Timber.e(e, "General error in fetchAllData")
            }
        }
    }
}
