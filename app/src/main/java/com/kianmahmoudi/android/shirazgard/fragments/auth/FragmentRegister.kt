package com.kianmahmoudi.android.shirazgard.fragments.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.kianmahmoudi.android.shirazgard.R
import com.kianmahmoudi.android.shirazgard.activities.HomeActivity
import com.kianmahmoudi.android.shirazgard.databinding.FragmentRegisterBinding
import com.kianmahmoudi.android.shirazgard.util.isValidPassword
import com.kianmahmoudi.android.shirazgard.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.kianmahmoudi.android.shirazgard.data.UiState
import com.kianmahmoudi.android.shirazgard.util.NetworkUtils
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FragmentRegister : Fragment(R.layout.fragment_register) {

    private lateinit var binding: FragmentRegisterBinding
    private val userViewModel by viewModels<UserViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginLink.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentRegister_to_fragmentLogin)
        }

        binding.registerButton.setOnClickListener {
            val userName = binding.editTextUserNameRegister.editText?.text.toString()
            val password = binding.editTextPasswordRegister.editText?.text.toString()

            // چک کردن خالی نبودن نام کاربری و رمز عبور
            if (userName.isBlank()) {
                Toast.makeText(requireContext(), "Username cannot be empty", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (password.isBlank()) {
                Toast.makeText(requireContext(), "Password cannot be empty", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            // چک کردن معتبر بودن رمز عبور
            if (!isValidPassword(password)) {
                Toast.makeText(requireContext(), "Password is not valid", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (NetworkUtils.isOnline(requireContext()))
            userViewModel.registerUser(userName, password)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            userViewModel.registerState.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is UiState.Loading -> {}
                    is UiState.Success -> {
                        Toast.makeText(
                            requireContext(),
                            "Register was successful",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        val intent = Intent(requireActivity(), HomeActivity::class.java)
                        startActivity(intent)
                    }

                    is UiState.Error -> {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }

                    UiState.Idle -> {}
                }
                if (result != UiState.Idle) {
                    userViewModel.resetRegisterState()
                }
            }
        }

    }

}