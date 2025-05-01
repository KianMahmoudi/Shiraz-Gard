package com.kianmahmoudi.android.shirazgard.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.kianmahmoudi.android.shirazgard.R
import com.kianmahmoudi.android.shirazgard.adapters.CommentsAdapter
import com.kianmahmoudi.android.shirazgard.data.UiState
import com.kianmahmoudi.android.shirazgard.databinding.FragmentPlaceDetailsBinding
import com.kianmahmoudi.android.shirazgard.util.EqualSpacingItemDecoration
import com.kianmahmoudi.android.shirazgard.util.NetworkUtils
import com.kianmahmoudi.android.shirazgard.viewmodel.CommentViewModel
import com.kianmahmoudi.android.shirazgard.viewmodel.FavoritePlacesViewModel
import com.kianmahmoudi.android.shirazgard.viewmodel.UserViewModel
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
    private val commentsAdapter: CommentsAdapter by lazy { CommentsAdapter("PlaceDetailsFragment") }
    private val usersViewModel: UserViewModel by viewModels<UserViewModel>()
    private val commentViewModel: CommentViewModel by navGraphViewModels(R.id.main_nav_graph) {
        defaultViewModelProviderFactory
    }

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

        binding.viewAllComments.setOnClickListener {
            findNavController().navigate(
                PlaceDetailsFragmentDirections.actionPlaceDetailsFragmentToCommentsFragment(
                    args.objectId
                )
            )
        }

        when (Locale.getDefault().language) {
            "en" -> binding.placeTitle.text = args.enName
            "fa" -> binding.placeTitle.text = args.faName
        }

        commentViewModel.getComments(args.objectId)

        lifecycleScope.launch {
            usersViewModel.usersProfileImages.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is UiState.Error -> {
                        Timber.e("Error fetching profile image: ${result.message}")
                    }

                    UiState.Idle -> {}
                    UiState.Loading -> {}
                    is UiState.Success -> {
                        result.data.let { urlMap ->
                            commentsAdapter.submitProfileImages(urlMap)
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            commentViewModel.comments.observe(viewLifecycleOwner) {
                when (it) {
                    is UiState.Error -> {
                        binding.animNoComments.visibility = View.VISIBLE
                        binding.animNoComments.playAnimation()
                    }

                    UiState.Idle -> {}
                    UiState.Loading -> {}
                    is UiState.Success -> {
                        commentsAdapter.submitComments(it.data)
                        val usersId = it.data.mapNotNull { it.getString("userId") }.toSet()
                        usersId.forEach { userId ->
                            usersViewModel.getProfileImage(userId)
                        }
                        if (it.data.isEmpty()) {
                            binding.animNoComments.visibility = View.VISIBLE
                            binding.animNoComments.playAnimation()
                        }
                    }
                }
            }
        }

        binding.commentsRv.apply {
            adapter = commentsAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(EqualSpacingItemDecoration(4))
        }

        lifecycleScope.launch {
            if (NetworkUtils.isOnline(requireContext()))
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
            findNavController().let {
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
                    if (NetworkUtils.isOnline(requireContext()))
                        favoritePlacesViewModel.removePlace(
                            ParseUser.getCurrentUser().objectId,
                            args.objectId
                        )
                    binding.favoriteIcon.setImageResource(R.drawable.favorite_24px)
                }
            } else {
                lifecycleScope.launch {
                    if (NetworkUtils.isOnline(requireContext()))
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