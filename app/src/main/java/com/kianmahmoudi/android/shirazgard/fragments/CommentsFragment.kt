package com.kianmahmoudi.android.shirazgard.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.kianmahmoudi.android.shirazgard.R
import com.kianmahmoudi.android.shirazgard.adapters.CommentsAdapter
import com.kianmahmoudi.android.shirazgard.data.UiState
import com.kianmahmoudi.android.shirazgard.databinding.FragmentCommentsBinding
import com.kianmahmoudi.android.shirazgard.util.EqualSpacingItemDecoration
import com.kianmahmoudi.android.shirazgard.viewmodel.CommentViewModel
import com.kianmahmoudi.android.shirazgard.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class CommentsFragment : Fragment(R.layout.fragment_comments) {

    private var _binding: FragmentCommentsBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<CommentsFragmentArgs>()
    private val commentViewModel: CommentViewModel by navGraphViewModels(R.id.main_nav_graph) {
        defaultViewModelProviderFactory
    }

    private val userViewModel: UserViewModel by viewModels()
    private val commentsAdapter by lazy {
        CommentsAdapter("CommentFragment")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        commentViewModel.getComments(args.placeId)

        lifecycleScope.launch {
            userViewModel.usersProfileImages.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is UiState.Error -> Timber.e("Error fetching profile image: ${result.message}")
                    is UiState.Success -> commentsAdapter.submitProfileImages(result.data)
                    else -> {}
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
                    is UiState.Success -> {
                        commentsAdapter.submitComments(it.data)
                        binding.rvComments.smoothScrollToPosition(0)
                        val usersId = it.data.mapNotNull { it.getString("userId") }.toSet()
                        usersId.forEach { userId ->
                            userViewModel.getProfileImage(userId)
                        }
                        if (it.data.isEmpty()) {
                            binding.animNoComments.visibility = View.VISIBLE
                            binding.animNoComments.playAnimation()
                        }
                    }
                    else -> {}
                }
            }
        }

        binding.rvComments.apply {
            adapter = commentsAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(EqualSpacingItemDecoration(4))
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
