package com.kianmahmoudi.android.shirazgard.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.kianmahmoudi.android.shirazgard.R
import com.kianmahmoudi.android.shirazgard.data.Result
import com.kianmahmoudi.android.shirazgard.databinding.FragmentEditProfileBinding
import com.kianmahmoudi.android.shirazgard.viewmodel.UserViewModel
import com.parse.ParseUser
import dagger.hilt.android.AndroidEntryPoint
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
        setupUI()
        observeViewModel()
        loadCurrentUserData()
    }

    private fun setupUI() {
        binding.btnUpload.setOnClickListener { openImagePicker() }
        binding.btnDelete.setOnClickListener { deleteProfileImage() }
        binding.btnSave.setOnClickListener { updateUsername() }
    }

    private fun observeViewModel() {
        userViewModel.profileImageState.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    Glide.with(requireContext())
                        .load(result.data)
                        .placeholder(R.drawable.person_24px)
                        .circleCrop()
                        .into(binding.profileImage)
                }

                is Result.Error -> {
                    showToast(result.message)
                }

                is Result.Loading -> {

                }

                null -> {}
            }

            userViewModel.profileImageDeletionState.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Loading -> {

                    }

                    is Result.Success -> {
                        showToast("تصویر پروفایل با موفقیت حذف شد")
                        binding.profileImage.setImageResource(R.drawable.person_24px)
                        userViewModel.resetProfileImgDeletionState()
                    }

                    is Result.Error -> {
                        showToast(result.message)
                        userViewModel.resetProfileImgDeletionState()
                    }

                    null -> {}
                }
            }

        }

        userViewModel.usernameState.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    showToast("نام کاربری با موفقیت به‌روزرسانی شد")
                    findNavController().navigateUp()
                }

                is Result.Error -> {
                    showToast(result.message)
                }

                is Result.Loading -> {

                }

                null -> {}
            }
        }


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
        if (newUsername.isNotEmpty()) {
            Timber.i("username: " + userViewModel.usernameState.value)
            userViewModel.updateUsername(newUsername)
            Timber.i("username: " + userViewModel.usernameState.value)
        } else {
            showToast("نام کاربری نمی‌تواند خالی باشد")
        }
    }

    private fun showToast(message: String) {
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT)
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

    override fun onResume() {
        super.onResume()
        loadCurrentUserData()
    }

    private fun loadCurrentUserData() {
        // دریافت داده‌های تازه از سرور
        ParseUser.getCurrentUser()?.fetchInBackground<ParseUser> { _, _ ->
            binding.etUsername.setText(ParseUser.getCurrentUser()?.username)
            userViewModel.fetchProfileImageUrl()
        }
    }

}