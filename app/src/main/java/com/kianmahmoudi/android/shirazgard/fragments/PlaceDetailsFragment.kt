package com.kianmahmoudi.android.shirazgard.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.kianmahmoudi.android.shirazgard.R
import com.kianmahmoudi.android.shirazgard.databinding.FragmentPlaceDetailsBinding
import com.kianmahmoudi.android.shirazgard.util.observeOnce
import com.kianmahmoudi.android.shirazgard.viewmodel.FavoritePlacesViewModel
import com.parse.ParseUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Locale

@AndroidEntryPoint
class PlaceDetailsFragment : Fragment(R.layout.fragment_place_details) {

    private lateinit var binding: FragmentPlaceDetailsBinding
    private val args: PlaceDetailsFragmentArgs by navArgs()
    private val favoritePlacesViewModel: FavoritePlacesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaceDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageList = ArrayList<SlideModel>()

        when (Locale.getDefault().language) {
            "en" -> binding.placeTitle.text = args.enName
            "fa" -> binding.placeTitle.text = args.faName
        }

        lifecycleScope.launch {
            favoritePlacesViewModel.checkIfPlaceIsFavorite(
                ParseUser.getCurrentUser().objectId,
                args.objectId
            )
            favoritePlacesViewModel.isFavorite.observe(viewLifecycleOwner) {
                if (it)
                    binding.favoriteIcon.setImageResource(R.drawable.favorite_red_24)
                else
                    binding.favoriteIcon.setImageResource(R.drawable.favorite_24px)
            }
        }

        binding.placeAddress.text = args.address
        binding.placeDescription.text = args.description

        val imageUrls = args.images

        for (url in imageUrls) {
            imageList.add(SlideModel(url, ScaleTypes.FIT))
        }

        binding.imageSlider.setImageList(imageList)

        binding.fabShowOnMap.setOnClickListener {
            findNavController()?.let {
                try {
                    val action =
                        PlaceDetailsFragmentDirections.actionPlaceDetailsFragmentToHomeActivity(
                            destination = "mapFragment",
                            latitude = args.latitude,
                            longitude = args.longitude
                        )
                    it.navigate(action)
                } catch (e: Exception) {
                    Timber.tag("detailsActivity").e(e.message.toString())
                }
            }
        }

        binding.favoriteIcon.setOnClickListener {
            val isCurrentlyFavorite = favoritePlacesViewModel.isFavorite.value == true
            if (isCurrentlyFavorite) {
                lifecycleScope.launch {
                    favoritePlacesViewModel.removePlace(
                        ParseUser.getCurrentUser().objectId,
                        args.objectId
                    )
                    binding.favoriteIcon.setImageResource(R.drawable.favorite_24px)
                }
            } else {
                lifecycleScope.launch {
                    favoritePlacesViewModel.addPlace(
                        ParseUser.getCurrentUser().objectId,
                        args.objectId
                    )
                    binding.favoriteIcon.setImageResource(R.drawable.favorite_red_24)
                }
            }
        }

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}