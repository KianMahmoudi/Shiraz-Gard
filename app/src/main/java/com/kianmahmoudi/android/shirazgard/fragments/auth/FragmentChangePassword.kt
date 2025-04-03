package com.kianmahmoudi.android.shirazgard.fragments.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.kianmahmoudi.android.shirazgard.R
import com.kianmahmoudi.android.shirazgard.databinding.FragmentChangePasswordBinding
import com.kianmahmoudi.android.shirazgard.util.isValidPassword
import com.kianmahmoudi.android.shirazgard.viewmodel.UserViewModel
import com.parse.ParseUser
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import com.kianmahmoudi.android.shirazgard.data.UiState
import com.yariksoffice.lingver.Lingver
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FragmentChangePassword : Fragment(R.layout.fragment_change_password) {

    private lateinit var binding: FragmentChangePasswordBinding
    private val userViewModel: UserViewModel by viewModels()
    private var isCurrentPasswordValid = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChangePasswordBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSubmit.setOnClickListener {
            if (binding.currentPasswordEditText.text?.isNotEmpty() == true) {
                userViewModel.verifyCurrentPassword(binding.currentPasswordEditText.text.toString())
            }
            if (allFieldsValid()) {
                if (ParseUser.getCurrentUser() != null) {
                    userViewModel.changePassword(
                        binding.newPasswordEditText.text.toString()
                    )
                }
            }
        }

        Timber.i("user: ${ParseUser.getCurrentUser()}")

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                userViewModel.passwordVerificationState.observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is UiState.Success -> {
                            isCurrentPasswordValid = result.data
                            binding.currentPasswordEditText.error = null
                        }

                        is UiState.Error -> {
                            if (result.message.equals("NU")) {
                                binding.currentPasswordEditText.error =
                                    getString(R.string.username_not_found)
                                isCurrentPasswordValid = false
                            } else if (result.message.equals("WP")) {
                                binding.currentPasswordEditText.error =
                                    getString(R.string.password_is_incorrect)
                                isCurrentPasswordValid = false
                            } else {
                                binding.currentPasswordEditText.error = result.message
                                isCurrentPasswordValid = false
                            }
                        }

                        is UiState.Loading -> {

                        }

                        UiState.Idle -> {
                            isCurrentPasswordValid = false
                            binding.currentPasswordEditText.error = null
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                userViewModel.passwordChangeState.observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is UiState.Success -> {
                            Toast.makeText(requireContext(), getString(R.string.password_changed), Toast.LENGTH_SHORT)
                                .show()
                            findNavController().navigateUp()
                        }

                        is UiState.Error -> {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.password_dont_changed),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        is UiState.Loading -> {
                        }

                        UiState.Idle -> {
                        }
                    }
                }
            }
        }
    }

    private fun allFieldsValid(): Boolean {
        var isValid = true

        if (!isCurrentPasswordValid) {
            isValid = false
        }

        if (!isValidPassword(binding.newPasswordEditText.text.toString())) {
            binding.newPasswordEditText.error = getString(R.string.password_is_not_valid)
            isValid = false
        } else {
            binding.newPasswordEditText.error = null
        }

        if (binding.newPasswordEditText.text.toString() != binding.repeatNewPasswordEditText.text.toString()) {
            binding.repeatNewPasswordEditText.error =
                getString(R.string.new_password_dont_match_repeated_password)
            isValid = false
        } else {
            binding.repeatNewPasswordEditText.error = null
        }

        return isValid
    }

    override fun onPause() {
        super.onPause()
        userViewModel.resetPasswordState()
    }

}