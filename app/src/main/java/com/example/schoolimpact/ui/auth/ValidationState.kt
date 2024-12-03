package com.example.schoolimpact.ui.auth

sealed class ValidationState {
    data object Initial : ValidationState()
    data object Valid : ValidationState()
    data class Invalid(val message: String) : ValidationState()
}