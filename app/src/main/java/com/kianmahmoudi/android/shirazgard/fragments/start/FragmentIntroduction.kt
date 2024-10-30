package com.kianmahmoudi.android.shirazgard.fragments.start

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.kianmahmoudi.android.shirazgard.R
import com.kianmahmoudi.android.shirazgard.activities.HomeActivity
import com.kianmahmoudi.android.shirazgard.databinding.FragmentIntroductionBinding
import com.parse.ParseUser

class FragmentIntroduction: Fragment(R.layout.fragment_introduction) {

    private lateinit var binding:FragmentIntroductionBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.startButton.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentIntroduction_to_fragmentAccountOption)
        }

        if (ParseUser.getCurrentUser() != null) {
            val intent = Intent(requireActivity(), HomeActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIntroductionBinding.inflate(inflater)
        return binding.root
    }

}