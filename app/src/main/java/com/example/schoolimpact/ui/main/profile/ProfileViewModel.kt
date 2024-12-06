package com.example.schoolimpact.ui.main.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolimpact.data.repository.AuthRepository
import kotlinx.coroutines.launch

class ProfileViewModel(private val authRepository: AuthRepository) : ViewModel() {


    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}