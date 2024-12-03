package com.example.schoolimpact

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.schoolimpact.data.repository.AuthRepository
import com.example.schoolimpact.di.Injection
import com.example.schoolimpact.ui.auth.AuthViewModel
import com.example.schoolimpact.ui.auth.login.LoginViewModel

class ViewModelFactory private constructor(
    private val authRepository: AuthRepository,
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return createViewModel(modelClass)
            ?: throw IllegalArgumentException("Unknown ViewModel Class: ${modelClass.name}")
    }

    private fun <T : ViewModel> createViewModel(modelClass: Class<T>): T? {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(authRepository) as T
            }

            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(authRepository) as T
            }

            else -> null
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory = instance ?: synchronized(this) {
            instance ?: ViewModelFactory(
                Injection.provideAuthRepository(context),
            )
        }.also { instance = it }
    }
}