package com.kianmahmoudi.android.shirazgard.fragments.Home


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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

        setupRecyclerViews()
        loadCategories()
        setupSwipeRefresh()
        observeData()
        updateUIState(UIState.LOADING)
    }

    private fun setupRecyclerViews() {
        // Categories RecyclerView
        binding.rvCategoriesHome.apply {
            layoutManager = GridLayoutManager(context, 3)
            adapter = categoriesHomeAdapter
            addItemDecoration(EqualSpacingItemDecoration(4))
        }

        // Hotels RecyclerView
        binding.rvHotelsHome.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = hotelsHomeAdapter
            addItemDecoration(EqualSpacingItemDecoration(4))
        }

        // Restaurants RecyclerView
        binding.rvRestaurantsHome.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = restaurantsHomeAdapter
            addItemDecoration(EqualSpacingItemDecoration(4))
        }
    }

    private fun loadCategories() {
        categoriesHomeAdapter.submitData(
            mutableListOf(
                Category(getString(R.string.atm), "atm", R.drawable.local_atm_24px, 0),
                Category(getString(R.string.hotels), "hotel", R.drawable.hotel_24px, 1),
                Category(getString(R.string.hospital), "hospital", R.drawable.home_health_24px, 2),
                Category(
                    getString(R.string.restaurant),
                    "restaurant",
                    R.drawable.restaurant_24px,
                    3
                ),
                Category(getString(R.string.wc), "wc", R.drawable.wc_24px, 4),
                Category(getString(R.string.parking), "parking", R.drawable.local_parking_24px, 5)
            )
        )
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            refreshAllData()
        }
    }

    private fun refreshAllData() {
        updateUIState(UIState.LOADING)
        homeViewModel.fetchAllData()
    }

    private fun observeData() {
        // Reset flags
        isWeatherLoaded = false
        areHotelsLoaded = false
        areRestaurantsLoaded = false
        areImagesLoaded = false

        homeViewModel.apply {
            weatherData.observe(viewLifecycleOwner) { weather ->
                binding.apply {
                    tvTemperatureHome.text = weather.temperature.toString()
                    tvDescriptionHome.text = weather.description
                    icWeatherHome.setImageResource(checkIcon(weather.icon))
                }
                isWeatherLoaded = true
                checkState()
            }

            weatherError.observe(viewLifecycleOwner) {
                isWeatherLoaded = true
                checkState()
                Toast.makeText(requireContext(), "Weather Load Error", Toast.LENGTH_SHORT).show()
            }

            images.observe(viewLifecycleOwner) { images ->
                restaurantsHomeAdapter.addImages(images)
                hotelsHomeAdapter.addImages(images)
                areImagesLoaded = true
                checkState()
            }

            places.observe(viewLifecycleOwner) { places ->
                val restaurants = places.filter { it.getString("type") == "restaurant" }
                val hotels = places.filter { it.getString("type") == "hotel" }
                hotelsHomeAdapter.addHotels(hotels)
                restaurantsHomeAdapter.addRestaurants(restaurants)
                areRestaurantsLoaded = true
                areHotelsLoaded = true
                checkState()
            }

        }
    }

    private fun checkState() {
        if (isWeatherLoaded && areHotelsLoaded && areRestaurantsLoaded && areImagesLoaded) {
            updateUIState(UIState.LOADED)
            binding.swipeRefresh.isRefreshing = false
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

    enum class UIState {
        LOADING,
        LOADED
    }
}