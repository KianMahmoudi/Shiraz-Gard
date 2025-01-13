package com.kianmahmoudi.android.shirazgard.fragments.Home


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.kianmahmoudi.android.shirazgard.R
import com.kianmahmoudi.android.shirazgard.adapters.CategoriesHomeAdapter
import com.kianmahmoudi.android.shirazgard.adapters.HotelsHomeAdapter
import com.kianmahmoudi.android.shirazgard.adapters.RestaurantsHomeAdapter
import com.kianmahmoudi.android.shirazgard.data.Category
import com.kianmahmoudi.android.shirazgard.databinding.FragmentHomeBinding
import com.kianmahmoudi.android.shirazgard.util.EqualSpacingItemDecoration
import com.kianmahmoudi.android.shirazgard.util.checkIcon
import com.kianmahmoudi.android.shirazgard.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel: HomeViewModel by viewModels()
    private val hotelsHomeAdapter: HotelsHomeAdapter by lazy {
        HotelsHomeAdapter()
    }
    private val restaurantsHomeAdapter: RestaurantsHomeAdapter by lazy {
        RestaurantsHomeAdapter()
    }
    private val categoriesHomeAdapter: CategoriesHomeAdapter by lazy {
        CategoriesHomeAdapter(findNavController())
    }
    private var isWeatherLoaded = false
    private var areHotelsLoaded = false
    private var areRestaurantsLoaded = false
    private var areImagesLoaded = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchCategoriesRv()
        fetchHotelsRv()
        fetchRestaurantsRv()

        loadCategories()

        updateUIState(UIState.LOADING)

        lifecycleScope.launch {
            observeData()
        }

    }

    private fun loadCategories() {
        categoriesHomeAdapter.submitData(
            mutableListOf(
                Category(getString(R.string.atm), "atm", R.drawable.local_atm_24px, 0),
                Category(getString(R.string.hotels), "hotel", R.drawable.hotel_24px, 1),
                Category(getString(R.string.hospital), "hospital", R.drawable.home_health_24px, 2),
                Category(getString(R.string.restaurant), "restaurant", R.drawable.restaurant_24px, 3),
                Category(getString(R.string.wc), "wc", R.drawable.wc_24px, 4),
                Category(getString(R.string.parking), "parking", R.drawable.local_parking_24px, 5)
            )
        )
    }

    private fun observeData() {
        homeViewModel.weatherData.observe(viewLifecycleOwner) {
            binding.tvTemperatureHome.text = it.temperature.toString()
            binding.tvDescriptionHome.text = it.description
            binding.icWeatherHome.setImageResource(checkIcon(it.icon))
            isWeatherLoaded = true
            checkState()
        }

        homeViewModel.images.observe(viewLifecycleOwner) {
            restaurantsHomeAdapter.addImages(it)
            hotelsHomeAdapter.addImages(it)
            areImagesLoaded = true
            checkState()
        }

        homeViewModel.weatherError.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
            isWeatherLoaded = true
            checkState()
        }

        homeViewModel.hotels.observe(viewLifecycleOwner) { hotels ->
            hotelsHomeAdapter.submitList(hotels)
            areHotelsLoaded = true
            checkState()
        }

        homeViewModel.restaurants.observe(viewLifecycleOwner) {
            restaurantsHomeAdapter.addRestaurants(it)
            areRestaurantsLoaded = true
            checkState()
        }
    }

    private fun checkState() {
        if (isWeatherLoaded && areHotelsLoaded && areRestaurantsLoaded && areImagesLoaded) {
            updateUIState(UIState.LOADED)
        }
    }

    private fun updateUIState(state: UIState) {
        val isLoading = state == UIState.LOADING
        binding.apply {
            progressBarHome.apply {
                if (isLoading) playAnimation() else cancelAnimation()
                visibility = if (isLoading) View.VISIBLE else View.GONE
            }
            listOf(
                rvCategoriesHome,
                rvHotelsHome,
                rvRestaurantsHome,
                cardView,
                textView3,
                textView4,
                textView5,
                textView6
            ).forEach { it.visibility = if (isLoading) View.GONE else View.VISIBLE }
        }
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

    private fun fetchRestaurantsRv() {
        val recyclerView = binding.rvRestaurantsHome
        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = restaurantsHomeAdapter
        recyclerView.addItemDecoration(EqualSpacingItemDecoration(4))
    }

    enum class UIState {
        LOADING,
        LOADED
    }

}