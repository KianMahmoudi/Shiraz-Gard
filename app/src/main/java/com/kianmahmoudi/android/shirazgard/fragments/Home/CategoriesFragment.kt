package com.kianmahmoudi.android.shirazgard.fragments

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.kianmahmoudi.android.shirazgard.R
import com.kianmahmoudi.android.shirazgard.adapters.CategoriesAdapter
import com.kianmahmoudi.android.shirazgard.data.Category
import com.kianmahmoudi.android.shirazgard.databinding.FragmentCategoriesBinding
import com.kianmahmoudi.android.shirazgard.util.EqualSpacingItemDecoration

class CategoriesFragment : Fragment(R.layout.fragment_categories) {

    private lateinit var binding: FragmentCategoriesBinding

    private val categoriesAdapter: CategoriesAdapter by lazy {
        CategoriesAdapter(findNavController(), "categories")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRvCategories()
        loadCategories()
    }

    private fun loadCategories() {
        categoriesAdapter.submitData(
            mutableListOf(
                Category(getString(R.string.atms), "atm", R.drawable.local_atm_24px, 0),
                Category(getString(R.string.hotels), "hotel", R.drawable.hotel_24px, 1),
                Category(getString(R.string.hospitals), "hospital", R.drawable.home_health_24px, 2),
                Category(
                    getString(R.string.restaurants),
                    "restaurant",
                    R.drawable.restaurant_24px,
                    3
                ),
                Category(getString(R.string.wcs), "wc", R.drawable.wc_24px, 4),
                Category(getString(R.string.parkings), "parking", R.drawable.local_parking_24px, 5),
                Category(getString(R.string.cultural_sites), "cultural_sites", R.drawable.museum_24px, 6),
                Category(getString(R.string.historical_sites), "historical_sites", R.drawable.temple_buddhist_24px, 7),
                Category(
                    getString(R.string.gas_stations),
                    "gas_station",
                    R.drawable.local_gas_station_24px,
                    8
                ),
                Category(
                    getString(R.string.car_washes),
                    "car_wash",
                    R.drawable.local_car_wash_24px,
                    9
                )
            )
        )
    }

    private fun setupRvCategories() {
        binding.rvCategories.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = categoriesAdapter
            addItemDecoration(EqualSpacingItemDecoration(4))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoriesBinding.inflate(inflater)
        return binding.root
    }

}