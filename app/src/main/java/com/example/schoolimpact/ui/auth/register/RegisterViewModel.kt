package com.example.schoolimpact.ui.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolimpact.data.repository.AuthRepository
import com.example.schoolimpact.ui.auth.AuthState
import com.example.schoolimpact.ui.auth.ValidationState
import com.example.schoolimpact.utils.Result
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class RegisterViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _nameState = MutableStateFlow<ValidationState>(ValidationState.Initial)
    val nameState = _nameState.asStateFlow()

    private val _emailState = MutableStateFlow<ValidationState>(ValidationState.Initial)
    val emailState = _emailState.asStateFlow()

    private val _educationLevelState = MutableStateFlow<ValidationState>(ValidationState.Initial)
    val educationLevelState = _educationLevelState.asStateFlow()

    private val _passwordState = MutableStateFlow<ValidationState>(ValidationState.Initial)
    val passwordState = _passwordState.asStateFlow()

    private val _registerState = MutableStateFlow<AuthState<*>>(AuthState.Initial)
    val registerState = _registerState

    private val _verificationState = MutableStateFlow<VerificationState>(VerificationState.Idle)
    val verificationState = _verificationState.asStateFlow()

    private val _showErrorAnimation = MutableSharedFlow<String>()
    val showErrorAnimation = _showErrorAnimation.asSharedFlow()

    private var currentName = ""
    private var currentEmail = ""
    private var currentEducationLevel = ""
    private var currentPassword = ""

    fun updateEmail(email: String) {
        currentEmail = email
        _emailState.value = validateEmail(email)
    }

    fun updateEducationLevel(level: String) {
        currentEducationLevel = level
        _educationLevelState.value = validateEducationLevel(level)
    }


    fun updatePassword(password: String) {
        currentPassword = password
        _passwordState.value = validatePassword(password)
    }

    fun register() {
        viewModelScope.launch {
            if (!validateCredentials()) {
                handleError("Invalid credentials")
                return@launch
            }
            _registerState.value = AuthState.Loading
            try {
                authRepository.login(currentEmail, currentPassword).catch { e ->
                    _registerState.value = AuthState.Error(e.message.toString())
                }.collect { user ->
                    _registerState.value = AuthState.Success(user)
                }
            } catch (e: Exception) {
                handleError(e.message.toString())
            }
        }
    }

    fun verifyEmail() {
        viewModelScope.launch {
            _verificationState.value = VerificationState.Loading
            authRepository.verifyEmail(currentEmail).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _verificationState.value = VerificationState.Success(result.data)
                    }

                    is Result.Error -> {
                        _verificationState.value = VerificationState.Error(result.error)
                    }

                    is Result.Loading -> {
                        _verificationState.value = VerificationState.Loading
                    }
                }
            }
        }
    }

    private fun validateCredentials(): Boolean {
        val nameValidation = validateName(currentName)
        val emailValidation = validateEmail(currentEmail)
        val educationValidation = validateEducationLevel(currentEducationLevel)
        val passwordValidation = validatePassword(currentPassword)

        _nameState.value = nameValidation
        _emailState.value = emailValidation
        _educationLevelState.value = educationValidation
        _passwordState.value = passwordValidation

        return nameValidation is ValidationState.Valid && emailValidation is ValidationState.Valid
                && passwordValidation is ValidationState.Valid && educationValidation is ValidationState.Valid
    }

    private fun validateName(name: String): ValidationState {
        return when {
            name.isBlank() -> ValidationState.Invalid("Name cannot be empty")
            else -> ValidationState.Valid
        }
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

    private fun validateEducationLevel(level: String): ValidationState {
        return when {
            level.isBlank() -> ValidationState.Invalid("Education level cannot be empty")
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

    private fun handleError(message: String) {
        viewModelScope.launch {
            _registerState.value = AuthState.Error(message)
            _showErrorAnimation.emit(message)
        }
    }

    fun resetStates() {
        _emailState.value = ValidationState.Initial
        _educationLevelState.value = ValidationState.Initial
        _passwordState.value = ValidationState.Initial
        currentEmail = ""
        currentPassword = ""
    }
}

sealed class VerificationState {
    data object Idle : VerificationState()
    data object Loading : VerificationState()
    data class Success(val message: String) : VerificationState()
    data class Error(val error: String) : VerificationState()
}
