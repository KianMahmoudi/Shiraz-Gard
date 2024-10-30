package com.kianmahmoudi.android.shirazgard.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kianmahmoudi.android.shirazgard.data.WeatherResult
import com.kianmahmoudi.android.shirazgard.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(private val weatherRepository: WeatherRepository) :
    ViewModel() {

    private val _weatherData = MutableLiveData<WeatherResult>()
    val weatherData: LiveData<WeatherResult> = _weatherData

    private val _weatherError = MutableLiveData<String>()
    val weatherError: LiveData<String> = _weatherError

    fun fetchWeather(city: String, licence: String) {
        viewModelScope.launch {
            try {
                val result = weatherRepository.getWeather(city, licence)
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

}