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
import com.bumptech.glide.Glide
import com.kianmahmoudi.android.shirazgard.R
import com.kianmahmoudi.android.shirazgard.adapters.CategoriesAdapter
import com.kianmahmoudi.android.shirazgard.adapters.PlacesHomeAdapter
import com.kianmahmoudi.android.shirazgard.data.Category
import com.kianmahmoudi.android.shirazgard.databinding.FragmentHomeBinding
import com.kianmahmoudi.android.shirazgard.dialog.NoInternetDialog
import com.kianmahmoudi.android.shirazgard.util.EqualSpacingItemDecoration
import com.kianmahmoudi.android.shirazgard.util.NetworkUtils
import com.kianmahmoudi.android.shirazgard.viewmodel.MainDataViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), NoInternetDialog.InternetDialogListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val mainDataViewModel: MainDataViewModel by viewModels()
    private val hotelsHomeAdapter: PlacesHomeAdapter by lazy { createPlaceAdapter() }
    private val restaurantsHomeAdapter: PlacesHomeAdapter by lazy { createPlaceAdapter() }
    private val categoriesAdapter: CategoriesAdapter by lazy {
        CategoriesAdapter(findNavController(), "home")
    }

    private var isNoInternetDialogShown = false
    private var isWeatherLoaded = false
    private var areHotelsLoaded = false
    private var areRestaurantsLoaded = false
    private var areImagesLoaded = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        loadCategories()
        setupSwipeRefresh()
        setupClickListeners()
        observeData()
        observeError()
        checkInternetAndLoadData()
    }

    private fun setupRecyclerViews() {
        binding.rvCategoriesHome.apply {
            layoutManager = GridLayoutManager(context, 3)
            adapter = categoriesAdapter
            addItemDecoration(EqualSpacingItemDecoration(4))
        }

        binding.rvHotelsHome.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = hotelsHomeAdapter
            addItemDecoration(EqualSpacingItemDecoration(4))
        }

        binding.rvRestaurantsHome.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = restaurantsHomeAdapter
            addItemDecoration(EqualSpacingItemDecoration(4))
        }
    }

    private fun loadCategories() {
        categoriesAdapter.submitData(
            listOf(
                Category(getString(R.string.atms), "atm", R.drawable.local_atm_24px, 0),
                Category(getString(R.string.hotels), "hotel", R.drawable.hotel_24px, 1),
                Category(getString(R.string.hospitals), "hospital", R.drawable.home_health_24px, 2),
                Category(getString(R.string.restaurant), "restaurant", R.drawable.restaurant_24px, 3),
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

    private fun setupClickListeners() {
        binding.btnSeeAllCategories.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToCategoriesFragment())
        }

        binding.btnSeeAllHotels.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToCategoryPlacesFragment(
                    "Hotels",
                    "hotel"
                )
            )
        }

        binding.btnSeeAllRestaurants.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToCategoryPlacesFragment(
                    "Restaurants",
                    "restaurant"
                )
            )
        }
    }

    private fun createPlaceAdapter() = PlacesHomeAdapter { item, images ->
        CoroutineScope(Dispatchers.IO).launch {
                val action = HomeFragmentDirections.actionHomeFragmentToPlaceDetailsFragment(
                    faName = item.getString("faName") ?: "",
                    enName = item.getString("enName") ?: "",
                    faAddress = item.getString("faAddress") ?: "",
                    enAddress = item.getString("enAddress") ?: "",
                    faDescription = item.getString("faDescription") ?: "",
                    enDescription = item.getString("enDescription") ?: "",
                    type = item.getString("type") ?: "",
                    latitude = item.getParseGeoPoint("location")?.latitude?.toFloat() ?: 0f,
                    longitude = item.getParseGeoPoint("location")?.longitude?.toFloat() ?: 0f,
                    objectId = item.objectId,
                    images = images?.toTypedArray()
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

    private fun checkInternetAndLoadData() {
        if (NetworkUtils.isOnline(requireContext())) {
            loadData()
        } else {
            showNoInternetDialog()
        }
    }

    private fun loadData() {
        updateUIState(UIState.LOADING)
        if (NetworkUtils.isOnline(requireContext()))
        mainDataViewModel.fetchAllData()
    }

    private fun refreshAllData() {
        if (NetworkUtils.isOnline(requireContext())) {
            loadData()
        } else {
            binding.swipeRefresh.isRefreshing = false
            showNoInternetDialog()
        }
    }

    private fun showNoInternetDialog() {
        if (!isNoInternetDialogShown) {
            isNoInternetDialogShown = true
            NoInternetDialog.newInstance(this).show(childFragmentManager, "NoInternetDialog")
        }
    }

    override fun onTryAgain() {
        isNoInternetDialogShown = false
        checkInternetAndLoadData()
    }

    override fun onExit() {
        isNoInternetDialogShown = false
        requireActivity().finish()
    }


    private fun observeError() {
        mainDataViewModel.dataLoadingError.observe(viewLifecycleOwner) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
                binding.swipeRefresh.isRefreshing = false
                updateUIState(UIState.LOADED)
            }
        }

        mainDataViewModel.weatherError.observe(viewLifecycleOwner) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Weather Load Error: $errorMessage", Toast.LENGTH_SHORT).show()
                binding.swipeRefresh.isRefreshing = false
                updateUIState(UIState.LOADED)
            }
        }
    }

    private fun observeData() {
        resetLoadedStates()
        mainDataViewModel.apply {
            weatherData.observe(viewLifecycleOwner) { weather ->
                binding.apply {
                    tvTemperatureHome.text = weather.temperature.toString()
                    tvDescriptionHome.text = weather.description
                    Glide.with(requireContext()).load("https:" + weather.icon).into(icWeatherHome)
                }
                isWeatherLoaded = true
                checkAllDataLoaded()
            }

            weatherError.observe(viewLifecycleOwner) {
                isWeatherLoaded = true
                checkAllDataLoaded()
                Toast.makeText(requireContext(), "Weather Load Error", Toast.LENGTH_SHORT).show()
            }

            images.observe(viewLifecycleOwner) { images ->
                restaurantsHomeAdapter.addImages(images)
                hotelsHomeAdapter.addImages(images)
                areImagesLoaded = true
                checkAllDataLoaded()
            }

            places.observe(viewLifecycleOwner) { places ->
                val restaurants = places.filter { it.getString("type") == "restaurant" }
                val hotels = places.filter { it.getString("type") == "hotel" }
                hotelsHomeAdapter.addPlaces(hotels)
                restaurantsHomeAdapter.addPlaces(restaurants)
                areRestaurantsLoaded = true
                areHotelsLoaded = true
                checkAllDataLoaded()
            }
        }
    }

    private fun resetLoadedStates() {
        isWeatherLoaded = false
        areHotelsLoaded = false
        areRestaurantsLoaded = false
        areImagesLoaded = false
    }

    private fun checkAllDataLoaded() {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    enum class UIState {
        LOADING, LOADED
    }
}