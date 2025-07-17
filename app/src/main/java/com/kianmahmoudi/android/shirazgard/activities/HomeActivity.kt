package com.kianmahmoudi.android.shirazgard.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import com.kianmahmoudi.android.shirazgard.MainNavGraphDirections
import com.kianmahmoudi.android.shirazgard.R
import com.kianmahmoudi.android.shirazgard.databinding.ActivityMainBinding
import com.kianmahmoudi.android.shirazgard.dialog.CommentBottomSheet
import com.kianmahmoudi.android.shirazgard.viewmodel.SettingViewModel
import com.parse.ParseUser
import com.yariksoffice.lingver.Lingver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Locale

@AndroidEntryPoint
class HomeActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val settingViewModel: SettingViewModel by viewModels()
    private var currentLanguage: String = Lingver.getInstance().getLanguage()

    // Flag برای جلوگیری از navigation همزمان
    private var isNavigating = false

    // Top-level destinations
    private val topLevelDestinations = setOf(
        R.id.homeFragment,
        R.id.favoriteFragment,
        R.id.mapFragment,
        R.id.categoriesFragment,
        R.id.settingFragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        observeSettings()

        if (ParseUser.getCurrentUser() == null) {
            startActivity(Intent(this, LoginRegisterActivity::class.java))
            finish()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBarLayout()
        setupNavigation()
        setupActionBar()
        handleIncomingIntent()
    }

    private fun setupActionBarLayout() {
        supportActionBar?.apply {
            setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM)
            setCustomView(R.layout.action_bar)
        }
    }

    private fun setupNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragmentHome) as? NavHostFragment

        navHostFragment?.let {
            navController = it.navController

            // تنظیم AppBarConfiguration با top-level destinations
            appBarConfiguration = AppBarConfiguration(topLevelDestinations)

            // تنظیم bottom navigation با custom listener
            setupBottomNavigation()
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavHome.setOnItemSelectedListener { item ->
            // جلوگیری از reselect همان item
            if (item.itemId == binding.bottomNavHome.selectedItemId) {
                return@setOnItemSelectedListener true
            }

            when (item.itemId) {
                R.id.homeFragment -> {
                    navigateToTopLevelDestination(R.id.homeFragment)
                    true
                }
                R.id.favoriteFragment -> {
                    navigateToTopLevelDestination(R.id.favoriteFragment)
                    true
                }
                R.id.mapFragment -> {
                    navigateToTopLevelDestination(R.id.mapFragment)
                    true
                }
                R.id.settingFragment -> {
                    navigateToTopLevelDestination(R.id.settingFragment)
                    true
                }
                R.id.categoriesFragment -> {
                    navigateToTopLevelDestination(R.id.categoriesFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun navigateToTopLevelDestination(destinationId: Int) {
        // جلوگیری از navigation همزمان
        if (isNavigating) return

        try {
            val currentDestination = navController.currentDestination?.id

            // اگر قبلاً در همین destination هستیم
            if (currentDestination == destinationId) return

            isNavigating = true

            // اگر در یک top-level destination هستیم
            if (currentDestination in topLevelDestinations) {
                navController.navigate(destinationId)
            } else {
                // اگر در fragment عمیق‌تری هستیم، ابتدا stack را پاک کنیم
                navController.popBackStack(R.id.homeFragment, false)
                if (destinationId != R.id.homeFragment) {
                    navController.navigate(destinationId)
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Navigation error to destination: $destinationId")
        } finally {
            // با تاخیر کوتاه flag را reset کنید
            Handler(Looper.getMainLooper()).postDelayed({
                isNavigating = false
            }, 300)
        }
    }

    private fun setupActionBar() {
        val actionBar = supportActionBar?.customView
        if (actionBar != null) {
            val tvTitle: TextView = actionBar.findViewById(R.id.tvTitle)
            val icSearch: ImageView = actionBar.findViewById(R.id.icSearch)
            val icBack: ImageView = actionBar.findViewById(R.id.icBack)
            val icAdd: ImageView = actionBar.findViewById(R.id.icAdd)

            icBack.setOnClickListener {
                onBackPressed()
            }

            icSearch.setOnClickListener {
                try {
                    if (!isNavigating) {
                        navController.navigate(R.id.searchFragment)
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Error navigating to search")
                }
            }

            icAdd.setOnClickListener {
                handleAddCommentClick()
            }

            navController.addOnDestinationChangedListener { _, destination, arguments ->
                updateUIForDestination(destination.id, arguments, tvTitle, icBack, icSearch, icAdd)
            }
        }
    }

    private fun updateUIForDestination(
        destinationId: Int,
        arguments: Bundle?,
        tvTitle: TextView,
        icBack: ImageView,
        icSearch: ImageView,
        icAdd: ImageView
    ) {
        when (destinationId) {
            R.id.placeDetailsFragment -> {
                binding.bottomNavHome.visibility = View.GONE
                icBack.visibility = View.VISIBLE
                icSearch.visibility = View.GONE
                icAdd.visibility = View.GONE
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
                binding.bottomNavHome.visibility = View.GONE
                icSearch.visibility = View.GONE
                icBack.visibility = View.VISIBLE
                icAdd.visibility = View.GONE
                tvTitle.text = getString(R.string.app_name)
                supportActionBar?.show()
            }

            R.id.searchFragment -> {
                supportActionBar?.hide()
                binding.bottomNavHome.visibility = View.GONE
                icAdd.visibility = View.GONE
            }

            R.id.fragmentChangePassword -> {
                binding.bottomNavHome.visibility = View.GONE
                icSearch.visibility = View.GONE
                icBack.visibility = View.VISIBLE
                icAdd.visibility = View.GONE
                tvTitle.text = getString(R.string.app_name)
                supportActionBar?.show()
            }

            R.id.editProfileFragment -> {
                binding.bottomNavHome.visibility = View.GONE
                icSearch.visibility = View.GONE
                icBack.visibility = View.VISIBLE
                icAdd.visibility = View.GONE
                tvTitle.text = getString(R.string.app_name)
                supportActionBar?.show()
            }

            R.id.commentsFragment -> {
                binding.bottomNavHome.visibility = View.GONE
                icSearch.visibility = View.GONE
                icAdd.visibility = View.VISIBLE
                icBack.visibility = View.VISIBLE
                tvTitle.text = getString(R.string.comments)
                supportActionBar?.show()
            }

            in topLevelDestinations -> {
                // Top-level destinations
                binding.bottomNavHome.visibility = View.VISIBLE
                icBack.visibility = View.GONE
                icSearch.visibility = View.VISIBLE
                icAdd.visibility = View.GONE

                // تنظیم title بر اساس destination
                tvTitle.text = when (destinationId) {
                    R.id.mapFragment -> getString(R.string.map)
                    R.id.favoriteFragment -> getString(R.string.favorite)
                    R.id.settingFragment -> getString(R.string.setting)
                    R.id.categoriesFragment -> getString(R.string.categories)
                    else -> getString(R.string.app_name)
                }
                supportActionBar?.show()

                // اطمینان از تنظیم صحیح bottom navigation
                updateBottomNavigationSelection(destinationId)
            }

            else -> {
                binding.bottomNavHome.visibility = View.VISIBLE
                icBack.visibility = View.GONE
                icSearch.visibility = View.VISIBLE
                icAdd.visibility = View.GONE
                tvTitle.text = getString(R.string.app_name)
                supportActionBar?.show()
            }
        }
    }

    private fun updateBottomNavigationSelection(destinationId: Int) {
        // بررسی کنیم که تغییری لازم است یا نه
        if (binding.bottomNavHome.selectedItemId == destinationId) return

        // استفاده از post برای اجرا در UI thread بعدی
        Handler(Looper.getMainLooper()).post {
            try {
                // موقتاً listener را غیرفعال کنیم
                binding.bottomNavHome.setOnItemSelectedListener(null)
                binding.bottomNavHome.selectedItemId = destinationId
                // دوباره listener را تنظیم کنیم
                setupBottomNavigation()
            } catch (e: Exception) {
                Timber.e(e, "Error updating bottom navigation selection")
            }
        }
    }

    private fun handleIncomingIntent() {
        val destination = intent.getStringExtra("destination") ?: return
        if (destination == "mapFragment") {
            val latitude = intent.getFloatExtra("latitude", 0.0F)
            val longitude = intent.getFloatExtra("longitude", 0.0F)

            // استفاده از post برای اطمینان از آماده بودن navController
            Handler(Looper.getMainLooper()).post {
                try {
                    val action = MainNavGraphDirections.actionGlobalMapFragment(
                        latitude = latitude,
                        longitude = longitude,
                        placeName = null
                    )
                    navController.navigate(action)
                } catch (e: Exception) {
                    Timber.e(e, "Error handling incoming intent")
                    // Fallback
                    binding.bottomNavHome.selectedItemId = R.id.mapFragment
                }
            }
        }
    }

    override fun onBackPressed() {
        if (isNavigating) return

        val currentDestination = navController.currentDestination?.id

        try {
            if (currentDestination in topLevelDestinations && currentDestination != R.id.homeFragment) {
                // اگر در top-level destination هستیم اما home نیست، به home برویم
                navigateToTopLevelDestination(R.id.homeFragment)
            } else if (!navController.navigateUp()) {
                // اگر نتوانستیم navigate up کنیم، activity را finish کنیم
                super.onBackPressed()
            }
        } catch (e: Exception) {
            Timber.e(e, "Error in onBackPressed")
            super.onBackPressed()
        }
    }

    private fun observeSettings() {
        lifecycleScope.launch {
            settingViewModel.languageFlow.collect { lang ->
                if (lang != currentLanguage) {
                    Lingver.getInstance().setLocale(applicationContext, lang)
                    recreate()
                    currentLanguage = lang
                }
            }
        }
        lifecycleScope.launch {
            settingViewModel.themeFlow.collect { theme ->
                updateTheme(theme)
            }
        }
    }

    private fun updateTheme(theme: String) {
        when (theme) {
            "system" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    private fun handleAddCommentClick() {
        when (navController.currentDestination?.id) {
            R.id.commentsFragment -> {
                val placeId = navController.currentBackStackEntry?.arguments?.getString("placeId")
                if (!placeId.isNullOrEmpty()) {
                    showCommentBottomSheet(placeId)
                } else {
                    showPlaceIdError()
                }
            }
            else -> Timber.d("Add button clicked in unrelated fragment")
        }
    }

    private fun showCommentBottomSheet(placeId: String) {
        CommentBottomSheet.newInstance(placeId).apply {
            show(supportFragmentManager, CommentBottomSheet.TAG)
        }
    }

    private fun showPlaceIdError() {
        Timber.e("placeId is null or empty, cannot show CommentBottomSheet")
        Snackbar.make(binding.root, "Error: Place not found", Snackbar.LENGTH_SHORT).show()
    }
}