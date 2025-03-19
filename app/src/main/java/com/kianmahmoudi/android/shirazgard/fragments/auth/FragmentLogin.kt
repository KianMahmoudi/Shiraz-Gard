package com.kianmahmoudi.android.shirazgard.fragments.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.kianmahmoudi.android.shirazgard.R
import com.kianmahmoudi.android.shirazgard.activities.HomeActivity
import com.kianmahmoudi.android.shirazgard.data.Result
import com.kianmahmoudi.android.shirazgard.databinding.FragmentLoginBinding
import com.kianmahmoudi.android.shirazgard.util.isValidPassword
import com.kianmahmoudi.android.shirazgard.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class FragmentLogin : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding
    private val userViewModel by viewModels<UserViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.dontHaveAccountLogin.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentLogin_to_fragmentRegister)
        }

        binding.loginButton.setOnClickListener {
            val userName = binding.editTextUserNameLogin.editText?.text.toString()
            val password = binding.editTextPasswordLogin.editText?.text.toString()
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

            if (!isValidPassword(password)) {
                Toast.makeText(requireContext(), "Password is not valid", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            userViewModel.loginUser(userName, password)
        }

        userViewModel.loginState.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {}
                is Result.Success -> {
                    Toast.makeText(requireContext(), "Login was successful", Toast.LENGTH_SHORT)
                        .show()
                    val intent = Intent(requireActivity(), HomeActivity::class.java)
                    startActivity(intent)
                }

                is Result.Error -> {
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                }

                null -> {}
            }
        }

    }
}