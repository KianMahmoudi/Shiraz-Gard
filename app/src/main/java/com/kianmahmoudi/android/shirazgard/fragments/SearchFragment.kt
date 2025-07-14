package com.kianmahmoudi.android.shirazgard.fragments

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.internal.ViewUtils.showKeyboard
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
    }
    private val mainDataViewModel: MainDataViewModel by viewModels()
    private lateinit var backDrawable: Drawable
    private lateinit var clearDrawable: Drawable

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

        clearDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_clear_24)!!
        backDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.arrow_back_24)!!

        setupRv()
        setupSearchView()

        mainDataViewModel.fetchAllData()

    }


    private fun setupRv() {
        binding.rvSearchResult.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = categoryPlacesAdapter
            addItemDecoration(EqualSpacingItemDecoration(4))
        }
    }

    private fun filterPlaces(query: String) {
        mainDataViewModel.places.observe(viewLifecycleOwner) { places ->
            println(places)
            mainDataViewModel.images.observe(viewLifecycleOwner) { images ->
                println(places)
                println(images)

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

                lifecycleScope.launch {
                    categoryPlacesAdapter.clearData()
                    if (filteredPlaces.isEmpty()) {
                        categoryPlacesAdapter.clearData()
                        binding.tvEmptySearch.visibility = View.GONE
                        binding.tvNoResults.visibility = View.VISIBLE
                    } else {
                        categoryPlacesAdapter.addPlaces(filteredPlaces)
                        binding.tvEmptySearch.visibility = View.GONE
                        binding.tvNoResults.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun setupSearchView() {
        updateClearButtonVisibility(false)

        binding.searchView.addTextChangedListener { editable ->
            val hasText = editable?.isNotEmpty() == true
            updateClearButtonVisibility(hasText)

            if (hasText) {
                filterPlaces(editable.toString())
                binding.tvEmptySearch.visibility = View.GONE
            } else {
                categoryPlacesAdapter.clearData()
                binding.tvEmptySearch.visibility = View.VISIBLE
                binding.tvNoResults.visibility = View.GONE
            }
        }

        // مدیریت کلیک روی drawableها
        binding.searchView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                // کلیک روی دکمه clear (سمت راست)
                if (isClickOnDrawableEnd(event)) {
                    binding.searchView.text?.clear()
                    return@setOnTouchListener true
                }
                // کلیک روی دکمه back (سمت چپ)
                else if (isClickOnDrawableStart(event)) {
                    findNavController().popBackStack()
                    return@setOnTouchListener true
                }
            }
            false
        }

        // مدیریت دکمه جستجو در کیبورد
        binding.searchView.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard()
                true
            } else {
                false
            }
        }

        // فوکوس خودکار روی searchView و نمایش کیبورد
        binding.searchView.requestFocus()
        showKeyboard(binding.searchView)
    }

    private fun updateClearButtonVisibility(visible: Boolean) {
        binding.searchView.setCompoundDrawablesWithIntrinsicBounds(
            backDrawable,
            null,
            if (visible) clearDrawable else null,
            null
        )
    }

    private fun isClickOnDrawableStart(event: MotionEvent): Boolean {
        val drawable = binding.searchView.compoundDrawables[0] ?: return false
        val clickPadding = 48.dpToPx(requireContext()) // افزایش ناحیه حساس به کلیک

        return event.x <= (binding.searchView.paddingStart + drawable.intrinsicWidth + clickPadding)
    }

    private fun isClickOnDrawableEnd(event: MotionEvent): Boolean {
        val drawable = binding.searchView.compoundDrawables[2] ?: return false
        val clickPadding = 48.dpToPx(requireContext()) // افزایش ناحیه حساس به کلیک

        return event.x >= (binding.searchView.width - binding.searchView.paddingEnd - drawable.intrinsicWidth - clickPadding)
    }

    private fun hideKeyboard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchView.windowToken, 0)
    }

    private fun showKeyboard(view: View) {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    fun Int.dpToPx(context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }

}
