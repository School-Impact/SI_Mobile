package com.example.schoolimpact.ui.auth.emailVerification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolimpact.data.repository.AuthRepository
import com.example.schoolimpact.ui.auth.ValidationState
import com.example.schoolimpact.ui.auth.register.VerificationState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EmailVerificationViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _emailState = MutableStateFlow<ValidationState>(ValidationState.Initial)
    val emailState = _emailState.asStateFlow()

    private val _verificationState = MutableStateFlow<VerificationState>(VerificationState.Idle)
    val verificationState = _verificationState.asStateFlow()

    private var currentEmail = ""

    fun updateEmail(email: String) {
        currentEmail = email
        _emailState.value = validateEmail(email)
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

    fun verifyEmail() {
        viewModelScope.launch {
            _verificationState.value = VerificationState.Loading
            try {
                val response = authRepository.verifyEmail(currentEmail)
                _verificationState.value = VerificationState.Success(response.message)
            } catch (e: Exception) {
                _verificationState.value = VerificationState.Error(e.message.toString())
            }
        }
    }
}