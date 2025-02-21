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
import com.kianmahmoudi.android.shirazgard.adapters.CategoriesAdapter
import com.kianmahmoudi.android.shirazgard.adapters.PlacesHomeAdapter
import com.kianmahmoudi.android.shirazgard.data.Category
import com.kianmahmoudi.android.shirazgard.databinding.FragmentHomeBinding
import com.kianmahmoudi.android.shirazgard.util.EqualSpacingItemDecoration
import com.kianmahmoudi.android.shirazgard.util.checkIcon
import com.kianmahmoudi.android.shirazgard.viewmodel.MainDataViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding
    private val mainDataViewModel: MainDataViewModel by viewModels()
    private val hotelsHomeAdapter: PlacesHomeAdapter by lazy { createPlaceAdapter() }
    private val restaurantsHomeAdapter: PlacesHomeAdapter by lazy { createPlaceAdapter() }
    private val categoriesAdapter: CategoriesAdapter by lazy {
        CategoriesAdapter(findNavController(), "home")
    }
    private var isWeatherLoaded = false
    private var areHotelsLoaded = false
    private var areRestaurantsLoaded = false
    private var areImagesLoaded = false

    private fun createPlaceAdapter() = PlacesHomeAdapter { item, images ->
        CoroutineScope(Dispatchers.IO).launch {
            if (!images.isNullOrEmpty()) {
                val action = HomeFragmentDirections.actionHomeFragmentToPlaceDetailsFragment(
                    faName = item.getString("faName") ?: "",
                    enName = item.getString("enName") ?: "",
                    address = item.getString("address") ?: "",
                    description = item.getString("description") ?: "",
                    type = item.getString("type") ?: "",
                    latitude = item.getParseGeoPoint("location")?.latitude?.toFloat() ?: 0f,
                    longitude = item.getParseGeoPoint("location")?.longitude?.toFloat() ?: 0f,
                    objectId = item.objectId,
                    images = images.toTypedArray()
                )
                try {
                    withContext(Dispatchers.Main) {
                        findNavController().navigate(action)
                    }
                } catch (e: Exception) {
                    Timber.e(e.message)
                }
            }
        }
    }

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

        binding.btnSeeAllCategories.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToCategoriesFragment())
        }

        binding.btnSeeAllHotels.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToCategoryPlacesFragment("Hotels","hotel"))
        }

        binding.btnSeeAllRestaurants.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToCategoryPlacesFragment("Restaurants","restaurant"))
        }

    }

    private fun setupRecyclerViews() {
        // Categories RecyclerView
        binding.rvCategoriesHome.apply {
            layoutManager = GridLayoutManager(context, 3)
            adapter = categoriesAdapter
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
        categoriesAdapter.submitData(
            mutableListOf(
                Category(getString(R.string.atms), "atm", R.drawable.local_atm_24px, 0),
                Category(getString(R.string.hotels), "hotel", R.drawable.hotel_24px, 1),
                Category(getString(R.string.hospitals), "hospital", R.drawable.home_health_24px, 2),
                Category(
                    getString(R.string.restaurant),
                    "restaurant",
                    R.drawable.restaurant_24px,
                    3
                ),
                Category(getString(R.string.wcs), "wc", R.drawable.wc_24px, 4),
                Category(getString(R.string.parkings), "parking", R.drawable.local_parking_24px, 5)
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
        mainDataViewModel.fetchAllData()
    }

    private fun observeData() {
        isWeatherLoaded = false
        areHotelsLoaded = false
        areRestaurantsLoaded = false
        areImagesLoaded = false

        mainDataViewModel.apply {
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
                hotelsHomeAdapter.addPlaces(hotels)
                restaurantsHomeAdapter.addPlaces(restaurants)
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
                btnSeeAllHotels,
                btnSeeAllCategories,
                btnSeeAllRestaurants,
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