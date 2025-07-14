package com.kianmahmoudi.android.shirazgard.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kianmahmoudi.android.shirazgard.R
import com.kianmahmoudi.android.shirazgard.adapters.CategoryPlacesAdapter
import com.kianmahmoudi.android.shirazgard.databinding.FragmentFavoriteBinding
import com.kianmahmoudi.android.shirazgard.dialog.NoInternetDialog
import com.kianmahmoudi.android.shirazgard.util.EqualSpacingItemDecoration
import com.kianmahmoudi.android.shirazgard.util.NetworkUtils
import com.kianmahmoudi.android.shirazgard.viewmodel.FavoritePlacesViewModel
import com.kianmahmoudi.android.shirazgard.viewmodel.MainDataViewModel
import com.parse.ParseUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

@AndroidEntryPoint
class FavoriteFragment : Fragment(R.layout.fragment_favorite),
    NoInternetDialog.InternetDialogListener {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private val favoritePlacesViewModel: FavoritePlacesViewModel by viewModels()
    private val mainDataViewModel: MainDataViewModel by viewModels()

    private var isNoInternetDialogShown = false

    private val adapter: CategoryPlacesAdapter by lazy {createPlaceAdapter()}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRvFavoritePlaces()
        setupObservers()
        loadData()
    }

    private fun setupRvFavoritePlaces() {
        binding.rvFavoritePlaces.apply {
            adapter = this@FavoriteFragment.adapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(EqualSpacingItemDecoration(4))
        }
    }

    private fun setupObservers() {
        favoritePlacesViewModel.favoritePlaces.observe(viewLifecycleOwner) { places ->

            val usersFavoritePlaces =
                places.filter { it.getString("userId") == ParseUser.getCurrentUser().objectId }

            val favoritePlaceIds = usersFavoritePlaces.map { it.getString("placeId") }

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

                    updateUIState(
                        if (filteredPlaces.isEmpty()) UIState.EMPTY
                        else UIState.LOADED
                    )
                }
            }
        }
    }

    private fun loadData() {
        if (NetworkUtils.isOnline(requireContext())) {
            updateUIState(UIState.LOADING)
            favoritePlacesViewModel.loadFavoritePlaces()
            mainDataViewModel.fetchAllData()
        } else {
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
        loadData()
    }

    override fun onExit() {
        isNoInternetDialogShown = false
        requireActivity().finish()
    }

    private fun updateUIState(state: UIState) {
        binding.apply {
            when (state) {
                UIState.LOADING -> {
                    progressBarFavoritePlaces.apply {
                        visibility = View.VISIBLE
                        playAnimation()
                    }
                    rvFavoritePlaces.visibility = View.GONE
                    emptyViewFavoritePlaces.apply {
                        visibility = View.GONE
                        cancelAnimation()
                    }
                    DontForgotToVisit.visibility = View.GONE
                }

                UIState.LOADED -> {
                    progressBarFavoritePlaces.apply {
                        visibility = View.GONE
                        cancelAnimation()
                    }
                    rvFavoritePlaces.visibility = View.VISIBLE
                    DontForgotToVisit.visibility = View.VISIBLE
                    emptyViewFavoritePlaces.apply {
                        visibility = View.GONE
                        cancelAnimation()
                    }
                }

                UIState.EMPTY -> {
                    progressBarFavoritePlaces.apply {
                        visibility = View.GONE
                        cancelAnimation()
                    }
                    rvFavoritePlaces.visibility = View.GONE
                    DontForgotToVisit.visibility = View.GONE
                    emptyViewFavoritePlaces.apply {
                        visibility = View.VISIBLE
                        playAnimation()
                    }
                }
            }
        }
    }

    private fun createPlaceAdapter() = CategoryPlacesAdapter { item, images ->
        CoroutineScope(Dispatchers.IO).launch {
            val action = FavoriteFragmentDirections.actionFavoriteFragmentToPlaceDetailsFragment(
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    enum class UIState {
        LOADING, LOADED, EMPTY
    }
}