package com.kianmahmoudi.android.shirazgard.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import com.kianmahmoudi.android.shirazgard.R
import com.kianmahmoudi.android.shirazgard.fragments.start.FragmentIntroduction
import com.parse.ParseUser
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginRegisterActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activty_login_register)

        supportActionBar?.apply {
            setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM)
            setCustomView(R.layout.action_bar)
        }

        val actionBar = supportActionBar?.customView
        if (actionBar != null) {
            actionBar.findViewById<ImageView>(R.id.icSearch).visibility = View.INVISIBLE
            actionBar.findViewById<ImageView>(R.id.icBack).visibility = View.INVISIBLE
            actionBar.findViewById<ImageView>(R.id.icAdd).visibility = View.INVISIBLE
        }

    }

}