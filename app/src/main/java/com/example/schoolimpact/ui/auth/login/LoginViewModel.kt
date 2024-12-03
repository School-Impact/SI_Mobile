package com.example.schoolimpact.ui.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolimpact.data.model.User
import com.example.schoolimpact.data.repository.AuthRepository
import com.example.schoolimpact.ui.auth.ValidationState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _emailState = MutableStateFlow()<ValidationState>(ValidationState.Initial)
    val emailState = _emailState.asStateFlow()

    private val _passwordState = MutableStateFlow<ValidationState>(ValidationState.Initial)
    val passwordState = _passwordState.asStateFlow()

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Initial)
    val loginState = _loginState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val result = authRepository.login(email, password)
                _loginState.value = LoginState.Success(result)
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Login failed")
            }
        }
    }

    fun register(name: String, email: String, educationLevel: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val result = authRepository.register(name, email, educationLevel, password)
                _loginState.value = LoginState.Success(result)
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Registration failed")
            }
        }
    }

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

}