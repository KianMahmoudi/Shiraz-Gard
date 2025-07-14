package com.kianmahmoudi.android.shirazgard.fragments.home

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
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kianmahmoudi.android.shirazgard.BuildConfig
import com.kianmahmoudi.android.shirazgard.R
import com.kianmahmoudi.android.shirazgard.activities.LoginRegisterActivity
import com.kianmahmoudi.android.shirazgard.data.UiState
import com.kianmahmoudi.android.shirazgard.databinding.FragmentSettingBinding
import com.kianmahmoudi.android.shirazgard.util.NetworkUtils
import com.kianmahmoudi.android.shirazgard.viewmodel.SettingViewModel
import com.kianmahmoudi.android.shirazgard.viewmodel.UserViewModel
import com.parse.ParseUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingFragment : Fragment(R.layout.fragment_setting) {

    lateinit var binding: FragmentSettingBinding
    private val settingViewModel: SettingViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.userName.text = ParseUser.getCurrentUser().username

        if (NetworkUtils.isOnline(requireContext()))
        userViewModel.fetchProfileImageUrl()

        binding.btnEditProfile.setOnClickListener {
            findNavController().navigate(SettingFragmentDirections.actionSettingFragmentToEditProfileFragment())
        }

        lifecycleScope.launch {
            settingViewModel.languageFlow.collect { lang ->
                binding.itemLang.title.text = getString(R.string.lang)
                binding.itemLang.icon.setImageResource(R.drawable.language_24px)
                binding.itemLang.root.setOnClickListener {
                    showLangDialog(if (lang == "fa") 0 else 1)
                }
            }
        }

        lifecycleScope.launch {
            settingViewModel.themeFlow.collect { theme ->
                binding.itemTheme.title.text = getString(R.string.theme)
                binding.itemTheme.icon.setImageResource(R.drawable.contrast_24px)
                binding.itemTheme.root.setOnClickListener {
                    showThemeDialog(if (theme == "system") 0 else if (theme == "light") 1 else 2)
                }
            }
        }

        //change passowrd
        binding.itemChangePassword.title.text = getString(R.string.change_password)
        binding.itemChangePassword.icon.setImageResource(R.drawable.lock_24px)
        binding.itemChangePassword.root.setOnClickListener {
            findNavController().navigate(SettingFragmentDirections.actionSettingFragmentToFragmentChangePassword())
        }

        //logout
        binding.itemLogout.title.text = getString(R.string.logout)
        binding.itemLogout.icon.setImageResource(R.drawable.logout_24px)
        binding.itemLogout.root.setOnClickListener {
            showLogoutDialog()
        }

        //delete account
        binding.itemDeleteAccount.title.text = getString(R.string.delete_account)
        binding.itemDeleteAccount.icon.setImageResource(R.drawable.delete_24px)
        binding.itemDeleteAccount.root.setOnClickListener {
            showDeleteAccountDialog()
        }

        //version
        binding.itemVersion.title.text =
            "${getString(R.string.version)}: ${BuildConfig.VERSION_NAME}"
        binding.itemVersion.icon.visibility = View.GONE
        binding.itemVersion.arrow.visibility = View.GONE

        observeUser()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingBinding.inflate(layoutInflater)
        return binding.root
    }

    private fun observeUser() {
        viewLifecycleOwner.lifecycleScope.launch {
            userViewModel.deleteAccountState.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is UiState.Success -> {
                        startActivity(
                            Intent(requireContext(), LoginRegisterActivity()::class.java)
                        )
                    }

                    is UiState.Error -> {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT)
                            .show()
                    }

                    is UiState.Loading -> {}
                    is UiState.Idle -> {}
                }
                if (result != UiState.Idle) {
                    userViewModel.resetDeleteAccountState()
                }
            }

        }
        viewLifecycleOwner.lifecycleScope.launch {
            userViewModel.profileImageState.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is UiState.Success -> {
                        Glide.with(requireContext())
                            .load(result.data)
                            .placeholder(R.drawable.person_24px)
                            .circleCrop()
                            .into(binding.userImage)
                    }

                    is UiState.Error -> {
                        binding.userImage.setImageResource(R.drawable.person_24px)
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
            userViewModel.logoutState.observe(viewLifecycleOwner){result->
                when(result){
                    is UiState.Error -> {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                    UiState.Idle -> {

                    }
                    UiState.Loading -> {

                    }
                    is UiState.Success -> {
                        startActivity(
                            Intent(requireContext(), LoginRegisterActivity()::class.java)
                        )
                    }
                }
                if (result != UiState.Idle) {
                    userViewModel.resetLogoutState()
                }
            }
        }
    }

    private fun showLangDialog(lang: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.choose_lang))
            .setSingleChoiceItems(
                arrayOf(
                    "پارسی", "english"
                ),
                lang
            ) { dialog, which ->
                when (which) {
                    0 -> {
                        lifecycleScope.launch {
                            settingViewModel.saveLanguage("fa")
                        }
                    }

                    1 -> {
                        lifecycleScope.launch {
                            settingViewModel.saveLanguage("en")
                        }
                    }
                }
            }
            .show()
    }

    private fun showThemeDialog(theme: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.choose_theme))
            .setSingleChoiceItems(
                arrayOf(
                    getString(R.string.system),
                    getString(R.string.light),
                    getString(R.string.dark)
                ),
                theme
            ) { dialog, which ->
                val selectedTheme = when (which) {
                    0 -> "system"
                    1 -> "light"
                    2 -> "dark"
                    else -> "system"
                }
                settingViewModel.saveTheme(selectedTheme)
                dialog.dismiss()
            }
            .show()
    }

    private fun showLogoutDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.are_you_sure))
            .setMessage(getString(R.string.are_you_sure_logout))
            .setPositiveButton(getString(R.string.yes)) { dialog, witch ->
                if (NetworkUtils.isOnline(requireContext()))
                userViewModel.logout()
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.no)) { dialog, witch ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showDeleteAccountDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.are_you_sure))
            .setMessage(getString(R.string.are_you_sure_delete_account))
            .setPositiveButton(getString(R.string.yes)) { dialog, witch ->
                if (NetworkUtils.isOnline(requireContext()))
                userViewModel.deleteAccount()
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.no)) { dialog, witch ->
                dialog.dismiss()
            }.show()
    }

}