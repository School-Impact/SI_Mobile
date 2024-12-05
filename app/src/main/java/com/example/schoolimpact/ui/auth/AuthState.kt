package com.example.schoolimpact.ui.auth

import com.example.schoolimpact.data.model.User
import com.example.schoolimpact.utils.Result

/**
 * Data validation state of the login form.
 */
sealed class AuthState<out T> {
    data object Initial : AuthState<Nothing>()
    data object Loading : AuthState<Nothing>()
    data class Success<T>(val user: T) : AuthState<T>()
    data class Error(val error: String) : AuthState<Nothing>()
}