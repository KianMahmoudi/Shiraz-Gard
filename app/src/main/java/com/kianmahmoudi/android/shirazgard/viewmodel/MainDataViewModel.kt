package com.kianmahmoudi.android.shirazgard.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kianmahmoudi.android.shirazgard.data.WeatherResult
import com.kianmahmoudi.android.shirazgard.repository.MainDataRepository
import com.kianmahmoudi.android.shirazgard.util.NetworkUtils
import com.parse.ParseObject
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class MainDataViewModel @Inject constructor(
    private val repository: MainDataRepository,
    @ApplicationContext private val context: Context
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

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _weatherState = MutableStateFlow<WeatherState>(WeatherState.Idle)
    val weatherState: StateFlow<WeatherState> = _weatherState

    fun fetchAllData() {
        if (!NetworkUtils.isOnline(context)) {
            _dataLoadingError.postValue("اتصال اینترنت برقرار نیست")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val imagesDeferred = async { fetchImages() }
                val placesDeferred = async { fetchPlaces() }
                val weatherDeferred = async { fetchWeather() }

                imagesDeferred.await()
                placesDeferred.await()
                weatherDeferred.await()

            } catch (e: Exception) {
                Timber.e(e, "General error in fetchAllData")
                _dataLoadingError.postValue("خطا در بارگذاری داده‌ها: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun fetchImages() {
        try {
            if (!NetworkUtils.isOnline(context)) {
                _dataLoadingError.postValue("اتصال اینترنت قطع شد")
                return
            }

            val images = repository.getPlaceImages()
            _images.postValue(images)
            Timber.d("Images loaded successfully: ${images.size}")

        } catch (e: SocketTimeoutException) {
            Timber.e(e, "Timeout fetching images")
            _dataLoadingError.postValue("زمان اتصال برای بارگذاری تصاویر به پایان رسید")
        } catch (e: IOException) {
            Timber.e(e, "IOError fetching images")
            _dataLoadingError.postValue("خطای شبکه در بارگذاری تصاویر")
        } catch (e: Exception) {
            Timber.e(e, "Error fetching images")
            _dataLoadingError.postValue("خطا در بارگذاری تصاویر: ${e.localizedMessage}")
        }
    }

    private suspend fun fetchPlaces() {
        try {
            if (!NetworkUtils.isOnline(context)) {
                _dataLoadingError.postValue("اتصال اینترنت قطع شد")
                return
            }

            val places = repository.getPlaces()
            _places.postValue(places)
            Timber.d("Places loaded successfully: ${places.size}")

        } catch (e: SocketTimeoutException) {
            Timber.e(e, "Timeout fetching places")
            _dataLoadingError.postValue("زمان اتصال برای بارگذاری مکان‌ها به پایان رسید")
        } catch (e: IOException) {
            Timber.e(e, "IOError fetching places")
            _dataLoadingError.postValue("خطای شبکه در بارگذاری مکان‌ها")
        } catch (e: Exception) {
            Timber.e(e, "Error fetching places")
            _dataLoadingError.postValue("خطا در بارگذاری مکان‌ها: ${e.localizedMessage}")
        }
    }

    private suspend fun fetchWeather() {
        try {
            if (!NetworkUtils.isOnline(context)) {
                _weatherError.postValue("اتصال اینترنت قطع شد")
                return
            }

            _weatherState.value = WeatherState.Loading

            val weather = repository.getWeather()
            weather?.let {
                _weatherData.postValue(it)
                _weatherState.value = WeatherState.Success(it)
                Timber.d("Weather loaded successfully")
            } ?: run {
                val errorMsg = "داده‌های آب و هوا دریافت نشد"
                _weatherError.postValue(errorMsg)
                _weatherState.value = WeatherState.Error(errorMsg)
            }

        } catch (e: UnknownHostException) {
            val errorMsg = "عدم دسترسی به سرور آب و هوا - ممکن است نیاز به VPN داشته باشید"
            Timber.e(e, "UnknownHostException fetching weather")
            _weatherError.postValue(errorMsg)
            _weatherState.value = WeatherState.Error(errorMsg)
        } catch (e: SocketTimeoutException) {
            val errorMsg = "زمان اتصال به سرور آب و هوا به پایان رسید"
            Timber.e(e, "Timeout fetching weather")
            _weatherError.postValue(errorMsg)
            _weatherState.value = WeatherState.Error(errorMsg)
        } catch (e: IOException) {
            val errorMsg = "خطای شبکه در دریافت آب و هوا"
            Timber.e(e, "IOError fetching weather")
            _weatherError.postValue(errorMsg)
            _weatherState.value = WeatherState.Error(errorMsg)
        } catch (e: Exception) {
            val errorMsg = "خطا در دریافت آب و هوا: ${e.localizedMessage}"
            Timber.e(e, "Error fetching weather")
            _weatherError.postValue(errorMsg)
            _weatherState.value = WeatherState.Error(errorMsg)
        }
    }

    fun retryWeather() {
        viewModelScope.launch {
            fetchWeather()
        }
    }

    fun retryAllData() {
        fetchAllData()
    }

    sealed class WeatherState {
        object Idle : WeatherState()
        object Loading : WeatherState()
        data class Success(val data: WeatherResult) : WeatherState()
        data class Error(val message: String) : WeatherState()
    }
}