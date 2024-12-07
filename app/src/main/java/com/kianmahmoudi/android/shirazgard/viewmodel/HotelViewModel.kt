package com.kianmahmoudi.android.shirazgard.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kianmahmoudi.android.shirazgard.repository.HotelRepository
import com.kianmahmoudi.android.shirazgard.repository.ParseHotelRepository
import kotlinx.coroutines.launch
import com.parse.ParseObject
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HotelViewModel @Inject constructor(private val hotelRepository: HotelRepository) :
    ViewModel() {

    private val _hotels = MutableLiveData<List<ParseObject>>()
    val hotels: LiveData<List<ParseObject>> get() = _hotels

    init {
        fetchHotels()
    }

    private fun fetchHotels() {
        viewModelScope.launch {
            try {
                val hotelList = hotelRepository.getHotels()
                _hotels.value = hotelList
            } catch (e: Exception) {

            }
        }
    }
}