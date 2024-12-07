package com.kianmahmoudi.android.shirazgard.fragments.Home


import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.kianmahmoudi.android.shirazgard.R
import com.kianmahmoudi.android.shirazgard.adapters.CategoriesHomeAdapter
import com.kianmahmoudi.android.shirazgard.adapters.HotelsHomeAdapter
import com.kianmahmoudi.android.shirazgard.data.Category
import com.kianmahmoudi.android.shirazgard.databinding.FragmentHomeBinding
import com.kianmahmoudi.android.shirazgard.repository.HotelRepository
import com.kianmahmoudi.android.shirazgard.repository.ParseHotelRepository
import com.kianmahmoudi.android.shirazgard.repository.WeatherRepository
import com.kianmahmoudi.android.shirazgard.util.EqualSpacingItemDecoration
import com.kianmahmoudi.android.shirazgard.util.checkIcon
import com.kianmahmoudi.android.shirazgard.viewmodel.HotelViewModel
import com.kianmahmoudi.android.shirazgard.viewmodel.WeatherViewModel
import com.parse.ParseObject
import com.parse.ParseQuery
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(com.kianmahmoudi.android.shirazgard.R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding
    private val weatherViewModel by viewModels<WeatherViewModel>()
    private val categoriesHomeAdapter: CategoriesHomeAdapter = CategoriesHomeAdapter()
    private val hotelViewModel: HotelViewModel by viewModels()

    private val hotelsHomeAdapter: HotelsHomeAdapter by lazy {
        HotelsHomeAdapter(provideHotelRepository())
    }
    private fun provideHotelRepository(): HotelRepository {
        return ParseHotelRepository()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchCategoriesRv()
        fetchHotelsRv()

        categoriesHomeAdapter.submitData(
            mutableListOf(
                Category(
                    getString(R.string.atm),
                    R.drawable.local_atm_24px, 0
                ),
                Category(
                    getString(R.string.hotel),
                    R.drawable.hotel_24px, 1
                ),
                Category(
                    getString(R.string.hospital),
                    R.drawable.home_health_24px, 2
                ),
                Category(
                    getString(R.string.restaurant),
                    R.drawable.restaurant_24px, 3
                ),
                Category(
                    getString(R.string.wc),
                    R.drawable.wc_24px, 4
                ),
                Category(
                    getString(R.string.parking),
                    R.drawable.local_parking_24px, 5
                ),
            )
        )

        weatherViewModel.fetchWeather(
            "شیراز",
            "mfggRHJXqMCI1yyQxPEsByuQzulsskdrAnESXdpnXu1Bq3iWtUai"
        )

        weatherViewModel.weatherData.observe(viewLifecycleOwner) {
            binding.tvTemperatureHome.text = it.temperature.toString()
            binding.tvDescriptionHome.text = it.description
            binding.icWeatherHome.setImageResource(checkIcon(it.icon))
        }

        weatherViewModel.weatherError.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
        }

        hotelViewModel.hotels.observe(viewLifecycleOwner, Observer { hotels ->
            hotelsHomeAdapter.submitList(hotels)
        })

    }

    private fun fetchCategoriesRv() {
        val recyclerView = binding.rvCategoriesHome
        recyclerView.layoutManager = GridLayoutManager(context, 3)
        recyclerView.adapter = categoriesHomeAdapter
        recyclerView.addItemDecoration(EqualSpacingItemDecoration(4))
    }

    private fun fetchHotelsRv() {
        val recyclerView = binding.rvHotelsHome
        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = hotelsHomeAdapter
        recyclerView.addItemDecoration(EqualSpacingItemDecoration(4))
    }

}