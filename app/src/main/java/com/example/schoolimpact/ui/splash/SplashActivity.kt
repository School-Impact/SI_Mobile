package com.example.schoolimpact.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.schoolimpact.MainActivity
import com.example.schoolimpact.data.preferences.AuthDataSource
import com.example.schoolimpact.ui.onboarding.OnboardingActivity
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private lateinit var authDataSource: AuthDataSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authDataSource = AuthDataSource(this)

        lifecycleScope.launch {
            navigateBasedOnAuthStatus()
        }

    }

    private suspend fun navigateBasedOnAuthStatus() {
        val isLoggedIn = authDataSource.isLoggedIn()
        val intent = if (isLoggedIn) {
            Intent(this@SplashActivity, MainActivity::class.java)
        } else {
            Intent(this@SplashActivity, OnboardingActivity::class.java)
        }
        startActivity(intent)
        finish()
    }

}