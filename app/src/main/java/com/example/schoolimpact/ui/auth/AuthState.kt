package com.example.schoolimpact.ui.auth

/**
 * Data validation state of the login form.
 */
sealed class AuthState<out T> {
    data object Initial : AuthState<Nothing>()
    data object Loading : AuthState<Nothing>()
    data class Success<T>(val user: T, val message: String) : AuthState<T>()
    data class Error(val error: String) : AuthState<Nothing>()
}