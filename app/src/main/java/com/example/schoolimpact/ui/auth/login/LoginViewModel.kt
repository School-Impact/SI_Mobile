package com.example.schoolimpact.ui.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolimpact.data.repository.AuthRepository
import com.example.schoolimpact.ui.auth.ValidationState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _emailState = MutableStateFlow<ValidationState>(ValidationState.Initial)
    val emailState = _emailState.asStateFlow()

    private val _passwordState = MutableStateFlow<ValidationState>(ValidationState.Initial)
    val passwordState = _passwordState.asStateFlow()

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Initial)
    val loginState = _loginState.asStateFlow()

    private var currentEmail = ""
    private var currentPassword = ""

    fun updateEmail(email: String) {
        currentEmail = email
        _emailState.value = validateEmail(email)
    }

    fun updatePassword(password: String) {
        currentPassword = password
        _passwordState.value = validatePassword(password)
    }

    fun login() {
        if (!validateCredentials()) return

        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val result = authRepository.login(currentEmail, currentPassword)
                _loginState.value = LoginState.Success(result)
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Login failed")
            }
        }
    }

    private fun validateCredentials(): Boolean {
        val emailValidation = validateEmail(currentEmail)
        val passwordValidation = validatePassword(currentPassword)

        _emailState.value = emailValidation
        _passwordState.value = passwordValidation

        return emailValidation is ValidationState.Valid && passwordValidation is ValidationState.Valid
    }

    private fun validateEmail(email: String): ValidationState {
        return when {
            email.isEmpty() -> ValidationState.Invalid("Email cannot be empty")
            !email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()) -> ValidationState.Invalid(
                "Invalid email format"
            )

            else -> ValidationState.Valid
        }
    }

    private fun validatePassword(password: String): ValidationState {
        return when {
            password.isEmpty() -> ValidationState.Invalid("Password cannot be empty")
            password.length < 8 -> ValidationState.Invalid("Password must be at least 8 characters")
            else -> ValidationState.Valid
        }
    }

    fun resetStates() {
        _emailState.value = ValidationState.Initial
        _passwordState.value = ValidationState.Initial
        _loginState.value = LoginState.Initial
        currentEmail = ""
        currentPassword = ""
    }
}
