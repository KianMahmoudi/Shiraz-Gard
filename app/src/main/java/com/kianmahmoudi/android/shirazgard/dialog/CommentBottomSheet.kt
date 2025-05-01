package com.kianmahmoudi.android.shirazgard.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.navGraphViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kianmahmoudi.android.shirazgard.R
import com.kianmahmoudi.android.shirazgard.data.UiState
import com.kianmahmoudi.android.shirazgard.databinding.BottomSheetCommentBinding
import com.kianmahmoudi.android.shirazgard.viewmodel.CommentViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class CommentBottomSheet : BottomSheetDialogFragment(R.layout.bottom_sheet_comment) {

    private var _binding: BottomSheetCommentBinding? = null
    private val binding get() = _binding!!
    private var placeId: String? = null
    private val commentViewModel: CommentViewModel by navGraphViewModels(R.id.main_nav_graph) {
        defaultViewModelProviderFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        placeId = arguments?.getString(ARG_PLACE_ID)
        Timber.d("Received placeId in CommentBottomSheet: $placeId")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetCommentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeViewModel()
    }

    private fun setupUI() = binding.run {
        cancelButton.setOnClickListener { dismiss() }

        submitButton.setOnClickListener {
            val commentText = commentEditText.text.toString().trim()
            val rating = ratingBar.rating

            if (placeId == null) {
                Timber.e("placeId is null, cannot submit comment")
                return@setOnClickListener
            }

            when {
                commentText.length < 3 -> showToast("Comment too short (min 3 chars)")
                rating < 1f -> showToast("Please provide a rating")
                else -> commentViewModel.addComment(placeId!!, commentText, rating.toInt())
            }
        }
    }

    private fun observeViewModel() {
        commentViewModel.addComment.observe(viewLifecycleOwner) { result ->
            when (result) {
                UiState.Loading -> showLoading(true)
                is UiState.Error -> {
                    showLoading(false)
                    showToast("Error: ${result.message}")
                }
                is UiState.Success -> {
                    showLoading(false)
                    commentViewModel.getComments(placeId!!)
                    resetInputs()
                    dismiss()
                }
                UiState.Idle -> showLoading(false)
            }
            if (result != UiState.Idle) commentViewModel.resetAddComment()
        }
    }

    private fun showLoading(isLoading: Boolean) = binding.run {
        submitButton.isEnabled = !isLoading
        loadingProgress.isVisible = isLoading
        submitButton.text = if (isLoading) "" else getString(R.string.submit)
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    private fun resetInputs() = binding.run {
        commentEditText.text?.clear()
        ratingBar.rating = 0f
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "CommentBottomSheet"
        private const val ARG_PLACE_ID = "place_id"
        fun newInstance(placeId: String): CommentBottomSheet {
            return CommentBottomSheet().apply {
                arguments = Bundle().apply {
                    putString(ARG_PLACE_ID, placeId)
                }
            }
        }
    }
}
