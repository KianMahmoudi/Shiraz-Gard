package com.kianmahmoudi.android.shirazgard.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.kianmahmoudi.android.shirazgard.R
import com.kianmahmoudi.android.shirazgard.data.Place
import com.kianmahmoudi.android.shirazgard.databinding.FragmentPlaceDetailsBinding
import com.kianmahmoudi.android.shirazgard.util.observeOnce
import com.kianmahmoudi.android.shirazgard.viewmodel.FavoritePlacesViewModel
import dagger.hilt.android.AndroidEntryPoint
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

        binding.apply {

            val imageList = ArrayList<SlideModel>()

            when (Locale.getDefault().language) {
                "en" -> placeTitle.text = args.enName ?: ""
                "fa" -> placeTitle.text = args.faName ?: ""
            }

            placeAddress.text = args.address ?: ""
            placeDescription.text = args.description ?: ""

            val imageUrls = args.images ?: emptyArray()

            for (url in imageUrls) {
                imageList.add(SlideModel(url, ScaleTypes.FIT))
            }

            imageSlider.setImageList(imageList) // No need for ScaleTypes here, set in SlideModel

            fabShowOnMap.setOnClickListener {
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

            favoritePlacesViewModel.getPlaceById(args.objectId).observe(viewLifecycleOwner) {
                if (it?.isFavorite == true)
                    binding.favoriteIcon.setImageResource(R.drawable.favorite_red_24)
                else if (it?.isFavorite == false)
                    binding.favoriteIcon.setImageResource(R.drawable.favorite_24px)
            }
            favoriteIcon.setOnClickListener {
                Timber.i("click")
                favoritePlacesViewModel.getPlaceById(args.objectId).observeOnce(viewLifecycleOwner) { place ->
                    place?.let {
                        Timber.i("click inside observer")
                        val newFavoriteState = !it.isFavorite
                        favoritePlacesViewModel.setFavorite(args.objectId, newFavoriteState)

                        binding.favoriteIcon.setImageResource(
                            if (newFavoriteState) R.drawable.favorite_red_24 else R.drawable.favorite_24px
                        )
                    }
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