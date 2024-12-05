package com.example.schoolimpact.ui.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolimpact.data.model.User
import com.example.schoolimpact.data.repository.AuthRepository
import com.example.schoolimpact.ui.auth.AuthState
import com.example.schoolimpact.ui.auth.ValidationState
import com.example.schoolimpact.utils.Result
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _emailState = MutableStateFlow<ValidationState>(ValidationState.Initial)
    val emailState = _emailState.asStateFlow()

    private val _passwordState = MutableStateFlow<ValidationState>(ValidationState.Initial)
    val passwordState = _passwordState.asStateFlow()

    private val _loginState = MutableStateFlow<AuthState<Result<User>>>(AuthState.Initial)
    val loginState = _loginState.asStateFlow()

    private val _showErrorAnimation = MutableSharedFlow<String>()
    val showErrorAnimation = _showErrorAnimation.asSharedFlow()

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
        viewModelScope.launch {
            if (!validateCredentials()) {
                handleError("Invalid credentials")
                return@launch
            }
            _loginState.value = AuthState.Loading
            try {
                authRepository.login(currentEmail, currentPassword).catch { e ->
                    _loginState.value = AuthState.Error(e.message.toString())
                }.collectLatest { user ->
                    _loginState.value = AuthState.Success(user)
                }
            } catch (e: Exception) {
                handleError(e.message.toString())
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

    private fun handleError(error: String) {
        viewModelScope.launch {
            _loginState.value = AuthState.Error(error)
            _showErrorAnimation.emit(error)
        }
    }

    fun resetStates() {
        _emailState.value = ValidationState.Initial
        _passwordState.value = ValidationState.Initial
        currentEmail = ""
        currentPassword = ""
    }
}
