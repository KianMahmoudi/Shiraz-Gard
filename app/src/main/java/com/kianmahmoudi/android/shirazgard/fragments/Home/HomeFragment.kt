package com.kianmahmoudi.android.shirazgard.fragments.Home


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.kianmahmoudi.android.shirazgard.R
import com.kianmahmoudi.android.shirazgard.adapters.CategoriesHomeAdapter
import com.kianmahmoudi.android.shirazgard.adapters.HotelsHomeAdapter
import com.kianmahmoudi.android.shirazgard.adapters.RestaurantsHomeAdapter
import com.kianmahmoudi.android.shirazgard.data.Category
import com.kianmahmoudi.android.shirazgard.databinding.FragmentHomeBinding
import com.kianmahmoudi.android.shirazgard.repository.HomeRepository
import com.kianmahmoudi.android.shirazgard.repository.ParseHomeRepository
import com.kianmahmoudi.android.shirazgard.util.EqualSpacingItemDecoration
import com.kianmahmoudi.android.shirazgard.util.checkIcon
import com.kianmahmoudi.android.shirazgard.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel: HomeViewModel by viewModels()
    private val hotelsHomeAdapter: HotelsHomeAdapter by lazy {
        HotelsHomeAdapter(provideHomeRepository())
    }
    private val restaurantsHomeAdapter: RestaurantsHomeAdapter by lazy {
        RestaurantsHomeAdapter(provideHomeRepository())
    }
    private val categoriesHomeAdapter: CategoriesHomeAdapter by lazy {
        CategoriesHomeAdapter()
    }
    private var a = false
    private var b = false
    private var c = false

    private fun provideHomeRepository(): HomeRepository {
        return ParseHomeRepository()
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
        fetchRestaurantsRv()

        checkLoadedItems()

        categoriesHomeAdapter.submitData(
            mutableListOf(
                Category(
                    getString(R.string.atm),
                    R.drawable.local_atm_24px, 0
                ),
                Category(
                    getString(R.string.hotels),
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

        homeViewModel.weatherData.observe(viewLifecycleOwner) {
            binding.tvTemperatureHome.text = it.temperature.toString()
            binding.tvDescriptionHome.text = it.description
            binding.icWeatherHome.setImageResource(checkIcon(it.icon))
            c = true
            checkLoadedItems()
        }

        homeViewModel.weatherError.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
            c = true
            checkLoadedItems()
        }

        homeViewModel.hotels.observe(viewLifecycleOwner) { hotels ->
            hotelsHomeAdapter.submitList(hotels)
            a = true
            checkLoadedItems()
        }

        homeViewModel.restaurants.observe(viewLifecycleOwner) {
            restaurantsHomeAdapter.submitList(it)
            b = true
            checkLoadedItems()
        }

    }

    private fun checkLoadedItems() {
        if (a && b && c) {
            binding.lottieAnimation.cancelAnimation()
            binding.lottieAnimation.visibility = View.GONE
            binding.rvCategoriesHome.visibility = View.VISIBLE
            binding.rvHotelsHome.visibility = View.VISIBLE
            binding.rvRestaurantsHome.visibility = View.VISIBLE
            binding.cardView.visibility = View.VISIBLE
            binding.textView3.visibility = View.VISIBLE
            binding.textView4.visibility = View.VISIBLE
            binding.textView5.visibility = View.VISIBLE
            binding.textView6.visibility = View.VISIBLE
        } else {
            binding.lottieAnimation.playAnimation()
            binding.lottieAnimation.visibility = View.VISIBLE
            binding.rvCategoriesHome.visibility = View.GONE
            binding.rvHotelsHome.visibility = View.GONE
            binding.rvRestaurantsHome.visibility = View.GONE
            binding.cardView.visibility = View.GONE
            binding.textView3.visibility = View.GONE
            binding.textView4.visibility = View.GONE
            binding.textView5.visibility = View.GONE
            binding.textView6.visibility = View.GONE
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

}