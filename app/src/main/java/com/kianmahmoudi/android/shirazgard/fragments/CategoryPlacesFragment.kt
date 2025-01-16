package com.kianmahmoudi.android.shirazgard.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.kianmahmoudi.android.shirazgard.R
import com.kianmahmoudi.android.shirazgard.adapters.CategoryPlacesAdapter
import com.kianmahmoudi.android.shirazgard.databinding.CategoryPlacesBinding
import com.kianmahmoudi.android.shirazgard.util.EqualSpacingItemDecoration
import com.kianmahmoudi.android.shirazgard.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategoryPlacesFragment : Fragment(R.layout.category_places) {

    private lateinit var binding: CategoryPlacesBinding
    private val homeViewModel: HomeViewModel by viewModels()
    private val args: CategoryPlacesFragmentArgs by navArgs()
    private val categoryPlacesAdapter: CategoryPlacesAdapter by lazy {
        CategoryPlacesAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CategoryPlacesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
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

    private fun setupObservers() {

        updateUIState(UIState.LOADING)

        lifecycleScope.launch {
            homeViewModel.places.observe(viewLifecycleOwner) { places ->
                val filteredPlaces = places.filter {
                    it.getString("type") == args.categoryType
                }

                homeViewModel.images.observe(viewLifecycleOwner) { images ->

                    categoryPlacesAdapter.addPlaces(filteredPlaces)
                    categoryPlacesAdapter.addImages(images)

                    if (filteredPlaces.isEmpty()) {
                        updateUIState(UIState.EMPTY)
                    } else {
                        updateUIState(UIState.LOADED)
                    }
                }
            }
        }
    }

    private fun setupToolbar() {
        binding.categoryName.text = args.categoryName
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {

                findNavController().navigateUp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onStop() {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        super.onStop()
    }

    override fun onResume() {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        super.onResume()
    }

}
