package com.example.schoolimpact

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.schoolimpact.data.repository.AuthRepository
import com.example.schoolimpact.data.repository.MajorRepository
import com.example.schoolimpact.di.Injection
import com.example.schoolimpact.ui.auth.login.LoginViewModel
import com.example.schoolimpact.ui.auth.register.RegisterViewModel
import com.example.schoolimpact.ui.main.discover.DiscoverViewModel
import com.example.schoolimpact.ui.main.profile.ProfileViewModel

class ViewModelFactory private constructor(
    private val authRepository: AuthRepository,
    private val majorRepository: MajorRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return createViewModel(modelClass)
            ?: throw IllegalArgumentException("Unknown ViewModel Class: ${modelClass.name}")
    }

    private fun <T : ViewModel> createViewModel(modelClass: Class<T>): T? {
        return when {

            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(authRepository) as T
            }

            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(authRepository) as T
            }

            modelClass.isAssignableFrom(DiscoverViewModel::class.java) -> {
                DiscoverViewModel(majorRepository) as T
            }

            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(authRepository) as T
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
                Injection.provideMajorRepository(context)
            )
        }.also { instance = it }
    }
}