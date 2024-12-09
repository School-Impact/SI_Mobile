package com.example.schoolimpact.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.schoolimpact.databinding.ActivityOnboardingBinding
import com.example.schoolimpact.ui.auth.AuthActivity
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var onboardingAdapter: OnboardingAdapter
    private val onboardingViewModel: OnboardingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setupAdapter()
        setOnboardingItems()

        TabLayoutMediator(binding.indicator, viewPager) { _, _ -> }.attach()

        binding.btnSignUp.setOnClickListener { navigateToAuthScreen() }
        binding.btnSignIn.setOnClickListener { navigateToAuthScreen() }
    }

    private fun setupAdapter() {
        onboardingAdapter = OnboardingAdapter()
        viewPager = binding.viewPager
        viewPager.adapter = onboardingAdapter
    }

    private fun setOnboardingItems() {
        lifecycleScope.launch {
            onboardingViewModel.onboardingItem.collectLatest { items ->
                onboardingAdapter.submitList(items)
            }
        }
    }

    private fun navigateToAuthScreen() {
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }
}