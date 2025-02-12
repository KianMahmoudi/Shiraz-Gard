package com.kianmahmoudi.android.shirazgard.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.kianmahmoudi.android.shirazgard.R
import com.kianmahmoudi.android.shirazgard.databinding.FragmentPlaceDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.Locale

@AndroidEntryPoint
class PlaceDetailsFragment : Fragment(R.layout.fragment_place_details) {

    private var binding: FragmentPlaceDetailsBinding? = null // Important: Make binding nullable
    private val args: PlaceDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? { // Return View?
        binding = FragmentPlaceDetailsBinding.inflate(inflater, container, false)
        return binding?.root // Use binding?.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply { // Use binding?.apply

            val imageList = ArrayList<SlideModel>()

            when (Locale.getDefault().language) {
                "en" -> {
                    placeTitle.text = args.enName ?: "" // Handle null arguments
                }
                "fa" -> {
                    placeTitle.text = args.faName ?: "" // Handle null arguments
                }
            }

            placeAddress.text = args.address ?: "" // Handle null arguments
            placeDescription.text = args.description ?: "" // Handle null arguments

            val imageUrls = args.images ?: emptyArray() // Handle null arguments, provide a default value

            for (url in imageUrls) {
                imageList.add(SlideModel(url, ScaleTypes.FIT))
            }

            fabShowOnMap.setOnClickListener {
                if (findNavController() != null) {
                    try {
                        val action =
                            PlaceDetailsFragmentDirections.actionPlaceDetailsFragmentToHomeActivity(
                                destination = "mapFragment",
                                latitude = args.latitude,
                                longitude = args.longitude
                            )
                        findNavController()!!.navigate(action)
                    } catch (e: Exception) {
                        Timber.tag("detailsActivity").e(e.message.toString())
                    }
                }
            }

            imageSlider.setImageList(imageList)
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

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null // Important: Set binding to null to avoid memory leaks
    }
}