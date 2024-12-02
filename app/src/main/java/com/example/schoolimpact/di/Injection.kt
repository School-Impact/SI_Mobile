package com.example.schoolimpact.di

import android.content.Context
import com.example.schoolimpact.data.api.ApiConfig
import com.example.schoolimpact.data.api.ApiService
import com.example.schoolimpact.data.repository.AuthRepository

object Injection {

    private fun provideApiService(): ApiService {
        return ApiConfig.getApiService()
    }

    fun provideAuthRepository(): AuthRepository {
        val apiService = provideApiService()
        return AuthRepository.getInstance(apiService)
    }


}