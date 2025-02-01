package com.kianmahmoudi.android.shirazgard.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navArgs
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kianmahmoudi.android.shirazgard.R

import com.kianmahmoudi.android.shirazgard.databinding.ActivityMainBinding
import com.kianmahmoudi.android.shirazgard.fragments.Home.HomeFragment
import com.parse.ParseUser
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        if (ParseUser.getCurrentUser() == null) {
            val intent = Intent(applicationContext, LoginRegisterActivity::class.java)
            startActivity(intent)
        }


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragmentHome) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNavHome.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.categoryPlacesFragment -> binding.bottomNavHome.visibility = View.INVISIBLE
                R.id.placeDetailsFragment -> binding.bottomNavHome.visibility = View.INVISIBLE
                else -> binding.bottomNavHome.visibility = View.VISIBLE
            }
        }

        handleIncomingIntent()

        supportActionBar?.apply {
            setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM)
            setCustomView(R.layout.action_bar)
        }
    }

    private fun handleIncomingIntent() {
        val destination = intent.getStringExtra("destination")
        if (destination == "mapFragment") {
            val bundle = Bundle().apply {
                putDouble("latitude", intent.getFloatExtra("latitude", 0.0F).toDouble())
                putDouble("longitude", intent.getFloatExtra("longitude", 0.0F).toDouble())
            }
            binding.bottomNavHome.selectedItemId = R.id.mapFragment
            navController.navigate(R.id.mapFragment, bundle)
        }
    }

}