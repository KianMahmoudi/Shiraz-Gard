package com.kianmahmoudi.android.shirazgard.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.kianmahmoudi.android.shirazgard.R
import com.parse.ParseUser

class LoginRegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setOnExitAnimationListener(listener = SplashScreen.OnExitAnimationListener {
            if (ParseUser.getCurrentUser() != null) {
                val intent = Intent(applicationContext, HomeActivity::class.java)
                startActivity(intent)
            }
        })
        setContentView(R.layout.activty_login_register)
    }
}