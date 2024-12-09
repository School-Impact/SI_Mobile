package com.example.schoolimpact.ui.onboarding

import androidx.lifecycle.ViewModel
import com.example.schoolimpact.R
import com.example.schoolimpact.data.model.OnboardingItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor() : ViewModel() {

    val _onboardingItem = MutableStateFlow<List<OnboardingItem>>(emptyList())
    val onboardingItem = _onboardingItem.asStateFlow()

    init {
        loadOnboardingItems()
    }


    private fun loadOnboardingItems() {
        _onboardingItem.value = listOf(
            OnboardingItem(R.drawable.onboarding_image1, "Title 1", "Description 1"),
            OnboardingItem(R.drawable.onboarding_image2, "Title 2", "Description 2"),
            OnboardingItem(R.drawable.onboarding_image3, "Title 3", "Description 3")
        )
    }
}