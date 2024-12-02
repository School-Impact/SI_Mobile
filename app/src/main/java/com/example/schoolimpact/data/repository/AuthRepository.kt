package com.example.schoolimpact.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.schoolimpact.data.api.ApiService
import com.example.schoolimpact.data.model.User
import com.example.schoolimpact.utils.Result
import okio.IOException

class AuthRepository private constructor(
    private val apiService: ApiService
) {

    fun login(email: String, password: String): LiveData<Result<User>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.login(email, password).loginResult
            val user = User(
                userId = response?.userId.toString(),
                name = response?.name.toString(),
                token = response?.token.toString()
            )
            emit(Result.Success(user))
        } catch (e: IOException) {
            emit(Result.Error("No Internet Connection"))
            Log.e(TAG, "Login : ${e.localizedMessage}")
        } catch (e: Exception) {
            Log.e(TAG, "Login : ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

//    suspend fun logout() = loginDataSource.logout()


    companion object {
        const val TAG = "Auth Repository"

        @Volatile
        private var instance: AuthRepository? = null
        fun getInstance(
            apiService: ApiService
        ): AuthRepository = instance ?: synchronized(this) {
            instance ?: AuthRepository(apiService)
        }.also { instance = it }
    }
}
