package com.example.schoolimpact.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolimpact.data.model.User
import com.example.schoolimpact.data.repository.AuthRepository
import kotlinx.coroutines.launch

class uthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    fun login(email: String, password: String) = authRepository.login(email, password)

    fun register(name: String, email: String, educationLevel: String, password: String) =
        authRepository.register(name, email, educationLevel, password)

    fun saveUser(user: User) {
        viewModelScope.launch {
            authRepository.saveUser(user)
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

}