package com.example.schoolimpact.ui.auth.login

import com.example.schoolimpact.data.model.User
import com.example.schoolimpact.utils.Result
import kotlinx.coroutines.flow.Flow

/**
 * Data validation state of the login form.
 */
sealed class LoginState {
    object Initial : LoginState()
    object Loading : LoginState()
    data class Success(val user: Flow<Result<User>>) : LoginState()
    data class Error(val message: String?) : LoginState()
}