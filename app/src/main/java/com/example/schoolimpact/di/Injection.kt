package com.example.schoolimpact.di

import android.content.Context
import com.example.schoolimpact.data.api.ApiConfig
import com.example.schoolimpact.data.api.ApiService
import com.example.schoolimpact.data.preferences.AuthDataSource
import com.example.schoolimpact.data.repository.AuthRepository
import com.example.schoolimpact.data.repository.MajorRepository

object Injection {

    private fun provideApiService(): ApiService {
        return ApiConfig.getApiService()
    }

    private fun provideAuthDataSource(context: Context): AuthDataSource {
        return AuthDataSource(context)
    }

    fun provideMajorRepository(context: Context): MajorRepository {
        val apiService = provideApiService()
        val authDataSource = provideAuthDataSource(context)
        return MajorRepository.getInstance(apiService, authDataSource)
    }


}