package com.kianmahmoudi.android.shirazgard.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.kianmahmoudi.android.shirazgard.R
import com.kianmahmoudi.android.shirazgard.adapters.CategoryPlacesAdapter
import com.kianmahmoudi.android.shirazgard.databinding.FragmentCategoryPlacesBinding
import com.kianmahmoudi.android.shirazgard.fragments.Home.HomeFragment
import com.kianmahmoudi.android.shirazgard.util.EqualSpacingItemDecoration
import com.kianmahmoudi.android.shirazgard.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

@AndroidEntryPoint
class CategoryPlacesFragment : Fragment(R.layout.fragment_category_places) {

    private lateinit var binding: FragmentCategoryPlacesBinding
    private val homeViewModel: HomeViewModel by viewModels()
    private val args: CategoryPlacesFragmentArgs by navArgs()
    private val categoryPlacesAdapter: CategoryPlacesAdapter by lazy {
        CategoryPlacesAdapter { item, images ->
            CoroutineScope(Dispatchers.IO).launch {
                if (!images.isNullOrEmpty()) {
                    val action =
                        CategoryPlacesFragmentDirections.actionCategoryPlacesFragmentToPlaceDetailsFragment(
                            faName = item.getString("faName") ?: "",
                            enName = item.getString("enName") ?: "",
                            address = item.getString("address") ?: "",
                            description = item.getString("description") ?: "",
                            type = item.getString("type") ?: "",
                            latitude = item.getParseGeoPoint("location")?.latitude?.toFloat()
                                ?: 0f,
                            longitude = item.getParseGeoPoint("location")?.longitude?.toFloat()
                                ?: 0f,
                            images = images.toTypedArray()
                        )
                    try {
                        withContext(Dispatchers.Main) {
                            findNavController().navigate(action)
                        }
                    } catch (e: Exception) {
                        Timber.e(e.message)
                    }
                } else {
                    Timber.tag("CategoryPlacesAdapter").d("No images found for place")
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryPlacesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSwipeRefresh()
        setupObservers()
        setupToolbar()
    }

    private fun setupRecyclerView() {
        binding.rvPlaces.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = categoryPlacesAdapter
            addItemDecoration(EqualSpacingItemDecoration(4))
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            refreshData()
        }
    }

    private fun refreshData() {
        updateUIState(UIState.LOADING)
        homeViewModel.fetchAllData()
    }

    private fun setupObservers() {
        updateUIState(UIState.LOADING)
        lifecycleScope.launch {
            homeViewModel.places.observe(viewLifecycleOwner) { places ->
                val filteredPlaces = places.filter {
                    it.getString("type") == args.categoryType
                }
                homeViewModel.images.observe(viewLifecycleOwner) { images ->
                    categoryPlacesAdapter.apply {
                        clearData()
                        addPlaces(filteredPlaces)
                        addImages(images)
                    }

                    if (filteredPlaces.isEmpty()) {
                        updateUIState(UIState.EMPTY)
                    } else {
                        updateUIState(UIState.LOADED)
                    }

                    binding.swipeRefresh.isRefreshing = false
                }
            }
        }
    }

    private fun setupToolbar() {
        binding.categoryName.text = args.categoryName
    }

    private fun updateUIState(state: UIState) {
        when (state) {
            UIState.LOADING -> {
                binding.apply {
                    progressBar.visibility = View.VISIBLE
                    progressBar.playAnimation()
                    categoryName.visibility = View.GONE
                    rvPlaces.visibility = View.GONE
                    emptyView.visibility = View.GONE
                    emptyView.cancelAnimation()
                }
            }

            UIState.LOADED -> {
                binding.apply {
                    progressBar.visibility = View.GONE
                    progressBar.cancelAnimation()
                    categoryName.visibility = View.VISIBLE
                    rvPlaces.visibility = View.VISIBLE
                    emptyView.visibility = View.GONE
                    emptyView.cancelAnimation()
                }
            }

            UIState.EMPTY -> {
                binding.apply {
                    progressBar.visibility = View.GONE
                    progressBar.cancelAnimation()
                    categoryName.visibility = View.GONE
                    rvPlaces.visibility = View.GONE
                    emptyView.visibility = View.VISIBLE
                    emptyView.playAnimation()
                }
            }
        }
    }

    enum class UIState {
        LOADING,
        LOADED,
        EMPTY
    }
}