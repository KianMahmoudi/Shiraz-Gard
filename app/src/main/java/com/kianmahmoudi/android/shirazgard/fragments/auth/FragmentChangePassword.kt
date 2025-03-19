package com.kianmahmoudi.android.shirazgard.fragments.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.kianmahmoudi.android.shirazgard.R
import com.kianmahmoudi.android.shirazgard.databinding.FragmentChangePasswordBinding
import com.kianmahmoudi.android.shirazgard.util.isValidPassword
import com.kianmahmoudi.android.shirazgard.viewmodel.UserViewModel
import com.parse.Parse
import com.parse.ParseUser
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import  com.kianmahmoudi.android.shirazgard.data.Result

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

        binding.currentPasswordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    if (ParseUser.getCurrentUser() != null)
                        userViewModel.verifyCurrentPassword(
                            it.toString()
                        )
                }
            }
        })

        Timber.i("user: ${ParseUser.getCurrentUser() == null}")

        userViewModel.passwordVerificationState.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    isCurrentPasswordValid = result.data
                }
                is Result.Error -> {
                    binding.currentPasswordEditText.error = result.message
                }
                is Result.Loading -> {

                }
            }
        }

        binding.btnSubmit.setOnClickListener {
            if (allFieldsValid()) {
                if (ParseUser.getCurrentUser() != null) {
                    userViewModel.changePassword(
                        binding.newPasswordEditText.text.toString()
                    )
                }
            }
        }

        userViewModel.passwordChangeState.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    Toast.makeText(requireContext(), "Password changed", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }

                is Result.Error -> {
                    Toast.makeText(requireContext(), "Password don't changed", Toast.LENGTH_SHORT)
                        .show()
                }

                is Result.Loading -> {
                }
            }
        }
    }

    private fun allFieldsValid(): Boolean {
        var isValid = true

        // check current password
        if (!isCurrentPasswordValid) {
            isValid = false
        }

        // check new password validation
        if (!isValidPassword(binding.newPasswordEditText.text.toString())) {
            binding.newPasswordEditText.error = getString(R.string.password_is_not_valid)
            isValid = false
        } else {
            binding.newPasswordEditText.error = null
        }

        // check if new passwords match
        if (binding.newPasswordEditText.text.toString() != binding.repeatNewPasswordEditText.text.toString()) {
            binding.repeatNewPasswordEditText.error =
                getString(R.string.new_password_dont_match_repeated_password)
            isValid = false
        } else {
            binding.repeatNewPasswordEditText.error = null
        }

        return isValid
    }
}