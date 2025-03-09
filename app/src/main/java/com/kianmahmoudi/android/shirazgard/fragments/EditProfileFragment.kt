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
import com.kianmahmoudi.android.shirazgard.databinding.FragmentEditProfileBinding
import com.kianmahmoudi.android.shirazgard.viewmodel.UserViewModel
import com.parse.ParseUser
import dagger.hilt.android.AndroidEntryPoint

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
        userViewModel.profileImageUrl.observe(viewLifecycleOwner) { url ->
            Glide.with(requireContext())
                .load(url)
                .placeholder(R.drawable.person_24px)
                .circleCrop()
                .into(binding.profileImage)
        }

        userViewModel.uploadProfileImageResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                showToast("تصویر پروفایل با موفقیت آپلود شد")
                userViewModel.resetUserNameAndProfileResults()
            }
        }

        userViewModel.uploadProfileImageError.observe(viewLifecycleOwner) { error ->
            error?.let { showToast(it) }
        }

        userViewModel.updateUsernameResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                showToast("نام کاربری با موفقیت به‌روزرسانی شد")
                userViewModel.resetUserNameAndProfileResults()
                findNavController().navigateUp()
            }
        }

        userViewModel.updateUsernameError.observe(viewLifecycleOwner) { error ->
            error?.let { showToast(it) }
        }
    }

    private fun loadCurrentUserData() {
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
        binding.profileImage.setImageResource(R.drawable.person_24px)
    }

    private fun updateUsername() {
        val newUsername = binding.etUsername.text.toString().trim()
        if (newUsername.isNotEmpty()) {
            userViewModel.updateUsername(newUsername)
        } else {
            showToast("نام کاربری نمی‌تواند خالی باشد")
        }
    }

    private fun showToast(message: String) {
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT).show()
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