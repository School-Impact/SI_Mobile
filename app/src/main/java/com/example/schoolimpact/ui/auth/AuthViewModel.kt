package com.example.schoolimpact.ui.auth

import androidx.lifecycle.ViewModel
import com.example.schoolimpact.data.repository.AuthRepository

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    fun login(email: String, password: String) = authRepository.login(email, password)

//    fun logout() {
//        viewModelScope.launch {
//            authRepository.logout()
//        }
//    }

}