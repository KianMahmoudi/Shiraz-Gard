package com.kianmahmoudi.android.shirazgard.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.kianmahmoudi.android.shirazgard.R
import dagger.hilt.android.AndroidEntryPoint
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.kianmahmoudi.android.shirazgard.databinding.ItemPlaceDetailsBinding
import timber.log.Timber
import java.util.Locale

@AndroidEntryPoint
class PlaceDetailsFragment : Fragment(R.layout.item_place_details) {

    private lateinit var binding: ItemPlaceDetailsBinding
    private val args: PlaceDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ItemPlaceDetailsBinding.inflate(inflater)
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
            "en" -> {
                binding.placeTitle.text = args.enName
            }
            "fa" -> {
                binding.placeTitle.text = args.faName
            }
        }

        binding.placeAddress.text = args.address
        binding.placeDescription.text = args.description

        val imageUrls = args.images

        for (url in imageUrls) {
            imageList.add(SlideModel(url, ScaleTypes.FIT))
        }

        binding.fabShowOnMap.setOnClickListener {
            try {
                val action =
                    PlaceDetailsFragmentDirections.actionPlaceDetailsFragmentToHomeActivity(
                        destination = "mapFragment",
                        latitude = args.latitude,
                        longitude = args.longitude
                    )
                findNavController().navigate(action)
            } catch (e: Exception) {
                Timber.tag("detailsActivity").e(e.message.toString())
            }
        }

        binding.imageSlider.setImageList(imageList)

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
