package com.kianmahmoudi.android.shirazgard.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kianmahmoudi.android.shirazgard.R
import com.kianmahmoudi.android.shirazgard.adapters.CategoryPlacesAdapter
import com.kianmahmoudi.android.shirazgard.databinding.FragmentSearchBinding
import com.kianmahmoudi.android.shirazgard.util.EqualSpacingItemDecoration
import com.kianmahmoudi.android.shirazgard.viewmodel.MainDataViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {

    private lateinit var binding: FragmentSearchBinding
    private val categoryPlacesAdapter: CategoryPlacesAdapter by lazy {
        CategoryPlacesAdapter { item, images ->
            CoroutineScope(Dispatchers.IO).launch {
                if (!images.isNullOrEmpty()) {
                    val action =
                        SearchFragmentDirections.actionSearchFragmentToPlaceDetailsFragment(
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
    private val mainDataViewModel: MainDataViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRv()
        setupSearchListener()

        binding.textField.endIconDrawable = null

        binding.textField.setStartIconOnClickListener {
            findNavController().navigateUp()
        }

        binding.textField.setEndIconOnClickListener {
            binding.etSearch.text?.clear()
            binding.textField.endIconDrawable = null
        }

        binding.textField.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding.textField.hint = "test"
            } else {
                binding.textField.hint = null
            }
        }



    }

    private fun setupSearchListener() {
        binding.etSearch.addTextChangedListener { query ->
            if (query.isNullOrEmpty()) {
                binding.textField.endIconDrawable = null // Hide the icon
            } else {
                binding.textField.endIconDrawable =
                    requireContext().getDrawable(R.drawable.baseline_clear_24) // Show the icon
            }
            filterPlaces(query.toString())
        }
    }

    private fun setupRv() {
        binding.rvSearchResult.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = categoryPlacesAdapter
            addItemDecoration(EqualSpacingItemDecoration(4))
        }
    }

    private fun filterPlaces(query: String) {
        lifecycleScope.launch {
            mainDataViewModel.places.observe(viewLifecycleOwner) { places ->
                mainDataViewModel.images.observe(viewLifecycleOwner) { images ->

                    if (query.isBlank()) {
                        categoryPlacesAdapter.clearData()
                        return@observe
                    }

                    val filteredPlaces = places.filter { place ->
                        val faName = place.getString("faName") ?: ""
                        val enName = place.getString("enName") ?: ""
                        faName.contains(query, ignoreCase = true) || enName.contains(
                            query,
                            ignoreCase = true
                        )
                    }

                    val filteredImages = images.filter { image ->
                        filteredPlaces.any { place ->
                            image.getString("placeId") == place.objectId
                        }
                    }

                    categoryPlacesAdapter.clearData()
                    if (filteredPlaces.isNotEmpty()) {
                        categoryPlacesAdapter.addPlaces(filteredPlaces)
                        categoryPlacesAdapter.addImages(filteredImages)
                    }
                }
            }
        }
    }
}
