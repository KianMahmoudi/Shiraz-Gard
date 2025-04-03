package com.kianmahmoudi.android.shirazgard.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.kianmahmoudi.android.shirazgard.R
import com.kianmahmoudi.android.shirazgard.data.UiState
import com.kianmahmoudi.android.shirazgard.databinding.FragmentEditProfileBinding
import com.kianmahmoudi.android.shirazgard.viewmodel.UserViewModel
import com.parse.ParseUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {

    private lateinit var binding: FragmentEditProfileBinding
    private val userViewModel: UserViewModel by viewModels()

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                uploadProfileImage(uri)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnUpload.setOnClickListener { openImagePicker() }
        binding.btnDelete.setOnClickListener { deleteProfileImage() }
        binding.btnSave.setOnClickListener {
            updateUsername()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            userViewModel.profileImageState.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is UiState.Success -> {
                        Glide.with(requireContext())
                            .load(result.data)
                            .placeholder(R.drawable.person_24px)
                            .circleCrop()
                            .into(binding.profileImage)
                    }

                    is UiState.Error -> {
                    }

                    is UiState.Loading -> {

                    }

                    UiState.Idle -> {}

                }
                if (result != UiState.Idle) {
                    userViewModel.resetProfileImageState()
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            userViewModel.profileImageDeletionState.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is UiState.Loading -> {

                    }

                    is UiState.Success -> {
                        binding.profileImage.setImageResource(R.drawable.person_24px)

                    }

                    is UiState.Error -> {
                    }

                    UiState.Idle -> {}
                }
                if (result != UiState.Idle) {
                    userViewModel.resetProfileImageDeletionState()
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            userViewModel.usernameState.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is UiState.Success -> {
                        findNavController().navigateUp()
                    }

                    is UiState.Error -> {
                    }

                    UiState.Loading -> {
                    }

                    UiState.Idle -> {
                    }
                }
                if (result != UiState.Idle) {
                    userViewModel.resetUsernameState()
                }
            }
        }

        val currentUser = ParseUser.getCurrentUser()
        binding.etUsername.setText(currentUser?.username)
        userViewModel.fetchProfileImageUrl()

    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        pickImageLauncher.launch(intent)
    }

    private fun uploadProfileImage(uri: Uri) {
        userViewModel.uploadProfileImage(uri)
    }

    private fun deleteProfileImage() {
        userViewModel.deleteProfileImage()
    }

    private fun updateUsername() {
        val newUsername = binding.etUsername.text.toString().trim()
        if (userViewModel.usernameState.value !is UiState.Loading) {
            if (newUsername.isNotEmpty()) {
                userViewModel.updateUsername(newUsername)
            } else {
                showToast("نام کاربری نمی‌تواند خالی باشد")
            }
        } else {
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
            .show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditProfileBinding.inflate(inflater)
        return binding.root
    }

}


