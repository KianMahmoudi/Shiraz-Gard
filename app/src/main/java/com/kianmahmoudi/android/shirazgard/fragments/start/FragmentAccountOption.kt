package com.kianmahmoudi.android.shirazgard.fragments.start

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.kianmahmoudi.android.shirazgard.R
import com.kianmahmoudi.android.shirazgard.databinding.FragmentAccountOptionsBinding

class FragmentAccountOption : Fragment() {

    private lateinit var binding: FragmentAccountOptionsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountOptionsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.registerButton.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentAccountOption_to_fragmentRegister)
        }

        binding.loginButton.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentAccountOption_to_fragmentLogin)
        }

    }

}