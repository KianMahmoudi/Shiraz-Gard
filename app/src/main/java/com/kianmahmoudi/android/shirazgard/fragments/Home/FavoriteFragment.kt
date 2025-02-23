package com.kianmahmoudi.android.shirazgard.fragments.Home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kianmahmoudi.android.shirazgard.R
import com.kianmahmoudi.android.shirazgard.adapters.CategoryPlacesAdapter
import com.kianmahmoudi.android.shirazgard.databinding.FragmentFavoriteBinding
import com.kianmahmoudi.android.shirazgard.fragments.CategoryPlacesFragment
import com.kianmahmoudi.android.shirazgard.util.EqualSpacingItemDecoration
import com.kianmahmoudi.android.shirazgard.viewmodel.FavoritePlacesViewModel
import com.kianmahmoudi.android.shirazgard.viewmodel.MainDataViewModel
import com.parse.ParseObject
import com.parse.ParseUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

@AndroidEntryPoint
class FavoriteFragment : Fragment(R.layout.fragment_favorite) {

    private lateinit var binding: FragmentFavoriteBinding
    private val favoritePlacesViewModel: FavoritePlacesViewModel by viewModels()
    private val mainDataViewModel: MainDataViewModel by viewModels()
    private val adapter: CategoryPlacesAdapter by lazy {
        CategoryPlacesAdapter { item, images ->
            CoroutineScope(Dispatchers.IO).launch {
                if (!images.isNullOrEmpty()) {
                    val action =
                        FavoriteFragmentDirections.actionFavoriteFragmentToPlaceDetailsFragment(
                            faName = item.getString("faName") ?: "",
                            enName = item.getString("enName") ?: "",
                            address = item.getString("address") ?: "",
                            description = item.getString("description") ?: "",
                            type = item.getString("type") ?: "",
                            latitude = item.getParseGeoPoint("location")?.latitude?.toFloat()
                                ?: 0f,
                            longitude = item.getParseGeoPoint("location")?.longitude?.toFloat()
                                ?: 0f,
                            images = images.toTypedArray(),
                            objectId = item.objectId
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRvFavoritePlaces()

        lifecycleScope.launch {
            updateUIState(UIState.LOADING)
            favoritePlacesViewModel.loadFavoritePlaces()
            favoritePlacesViewModel.favoritePlaces.observe(viewLifecycleOwner) { places ->
                val favoritePlaceIds = places.map { it.getString("placeId") }
                mainDataViewModel.places.observe(viewLifecycleOwner) { allPlaces ->
                    val filteredPlaces = allPlaces.filter { place ->
                        favoritePlaceIds.contains(place.objectId)
                    }
                    mainDataViewModel.images.observe(viewLifecycleOwner) { allImages ->
                        val filteredImages = allImages.filter { image ->
                            favoritePlaceIds.contains(image.getString("placeId"))
                        }
                        adapter.addPlaces(filteredPlaces)
                        adapter.addImages(filteredImages)

                        if (filteredPlaces.isEmpty()) {
                            updateUIState(UIState.EMPTY)
                        } else {
                            updateUIState(UIState.LOADED)
                        }

                    }
                }
            }
        }
    }

    private fun setupRvFavoritePlaces() {
        binding.rvFavoritePlaces.adapter = adapter
        binding.rvFavoritePlaces.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvFavoritePlaces.addItemDecoration(EqualSpacingItemDecoration(4))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteBinding.inflate(inflater)
        return binding.root
    }


    private fun updateUIState(state: UIState) {
        when (state) {
            UIState.LOADING -> {
                binding.apply {
                    progressBarFavoritePlaces.visibility = View.VISIBLE
                    progressBarFavoritePlaces.playAnimation()
                    rvFavoritePlaces.visibility = View.GONE
                    emptyViewFavoritePlaces.visibility = View.GONE
                    emptyViewFavoritePlaces.cancelAnimation()
                    DontForgotToVisit.visibility = View.GONE
                }
            }
            UIState.LOADED -> {
                binding.apply {
                    progressBarFavoritePlaces.visibility = View.GONE
                    progressBarFavoritePlaces.cancelAnimation()
                    DontForgotToVisit.visibility = View.VISIBLE
                    rvFavoritePlaces.visibility = View.VISIBLE
                    emptyViewFavoritePlaces.visibility = View.GONE
                    emptyViewFavoritePlaces.cancelAnimation()
                }
            }
            UIState.EMPTY -> {
                binding.apply {
                    progressBarFavoritePlaces.visibility = View.GONE
                    progressBarFavoritePlaces.cancelAnimation()
                    rvFavoritePlaces.visibility = View.GONE
                    DontForgotToVisit.visibility = View.GONE
                    emptyViewFavoritePlaces.visibility = View.VISIBLE
                    emptyViewFavoritePlaces.playAnimation()
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