package com.kianmahmoudi.android.shirazgard.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.kianmahmoudi.android.shirazgard.R
import com.kianmahmoudi.android.shirazgard.databinding.ActivityMainBinding
import com.parse.ParseUser
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        if (ParseUser .getCurrentUser () == null) {
            startActivity(Intent(this, LoginRegisterActivity::class.java))
            finish()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM)
            setCustomView(R.layout.action_bar)
        }

        val actionBar = supportActionBar?.customView
        if (actionBar != null) {
            val tvTitle: TextView = actionBar.findViewById(R.id.tvTitle)
            val icSearch: ImageView = actionBar.findViewById(R.id.icSearch)
            val icBack: ImageView = actionBar.findViewById(R.id.icBack)

            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.navHostFragmentHome) as? NavHostFragment
            navHostFragment?.let {
                navController = it.navController
                binding.bottomNavHome.setupWithNavController(navController)
            }

            icBack.setOnClickListener {
                navController.navigateUp()
            }

            icSearch.setOnClickListener {
                navController.navigate(R.id.searchFragment)
            }

            navController.addOnDestinationChangedListener { _, destination, arguments ->
                when (destination.id) {
                    R.id.placeDetailsFragment -> {
                        binding.bottomNavHome.visibility = View.INVISIBLE
                        icBack.visibility = View.VISIBLE
                        icSearch.visibility = View.INVISIBLE
                        val isPersian = Locale.getDefault().language == "fa"
                        val title = if (isPersian) {
                            arguments?.getString("faName") ?: "جزئیات مکان"
                        } else {
                            arguments?.getString("enName") ?: "Place Details"
                        }
                        tvTitle.text = title
                        supportActionBar?.show()
                    }

                    R.id.categoryPlacesFragment -> {
                        binding.bottomNavHome.visibility = View.INVISIBLE
                        icSearch.visibility = View.INVISIBLE
                        icBack.visibility = View.VISIBLE
                        tvTitle.text = getString(R.string.app_name)
                        supportActionBar?.show()
                    }
                    R.id.searchFragment -> {
                        supportActionBar?.hide()
                        binding.bottomNavHome.visibility = View.INVISIBLE
                    }
                    else -> {
                        binding.bottomNavHome.visibility = View.VISIBLE
                        icBack.visibility = View.GONE
                        icSearch.visibility = View.VISIBLE
                        tvTitle.text = getString(R.string.app_name)
                        supportActionBar?.show()
                    }
                }
            }
        }

        handleIncomingIntent()
    }

    private fun handleIncomingIntent() {
        val destination = intent.getStringExtra("destination") ?: return
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