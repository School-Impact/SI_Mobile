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
<<<<<<< HEAD
            OnboardingItem(R.drawable.onboarding_image1, " Rekomendasi Sekolah", "Description 1"),
            OnboardingItem(R.drawable.onboarding_image2, "Jurusan Sekolah Tingkat Atas", "Description 2"),
            OnboardingItem(R.drawable.onboarding_image3, "Profil", "Description 3")
=======
            OnboardingItem(R.drawable.onboarding_image1, "Kenali Potensimu!", "Jelajahi bakat dan minatmu untuk memilih jurusan yang terbaik."),
            OnboardingItem(R.drawable.onboarding_image2, "Pilihan Tepat Untuk Masa Depan Cerah", "Dapatkan rekomendasi jurusan berdasarkan penilaian dan preferensimu."),
            OnboardingItem(R.drawable.onboarding_image3, "Siap Melangkah?", "Awali perjalananmu menuju masa depan yang gemilang.")
>>>>>>> master
        )
    }
}