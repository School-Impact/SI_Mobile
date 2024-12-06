package com.example.schoolimpact.data.repository

import android.util.Log
import com.example.schoolimpact.data.api.ApiService
import com.example.schoolimpact.data.model.ErrorResponse
import com.example.schoolimpact.data.model.LoginResponse
import com.example.schoolimpact.data.model.RegisterResponse
import com.example.schoolimpact.data.model.User
import com.example.schoolimpact.data.model.UserData
import com.example.schoolimpact.data.preferences.AuthDataSource
import com.example.schoolimpact.ui.auth.AuthState
import com.example.schoolimpact.utils.Result
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import retrofit2.HttpException

class AuthRepository private constructor(
    private val apiService: ApiService, private val authDataSource: AuthDataSource
) {

    fun login(email: String, password: String): Flow<AuthState<LoginResponse>> = flow {
        emit(AuthState.Loading)
        try {
            val emailRequestBody = email.toRequestBody("text/plain".toMediaType())
            val passwordRequestBody = password.toRequestBody("text/plain".toMediaType())
            val response = apiService.login(emailRequestBody, passwordRequestBody)
            val user = response.data?.copy(token = response.token.toString())
            emit(AuthState.Success(user, response.message.toString()))
        } catch (e: HttpException) {
            val errorResult = parseHttpException(e)
            emit(errorResult)
        } catch (e: IOException) {
            emit(AuthState.Error("No Internet Connection"))
            Log.e(TAG, "Network Exception : ${e.localizedMessage}")
        } catch (e: Exception) {
            Log.e(TAG, "Login : ${e.message.toString()}")
            emit(AuthState.Error(e.message.toString()))
        }
    }

    fun register(
        name: String,
        email: String,
        educationLevel: String,
        phoneNumber: String,
        password: String
    ): Flow<AuthState<RegisterResponse>> = flow {
        emit(AuthState.Loading)
        try {
            val response = apiService.register(
                name.toRequestBody("text/plain".toMediaType()),
                email.toRequestBody("text/plain".toMediaType()),
                educationLevel.toRequestBody("text/plain".toMediaType()),
                phoneNumber.toRequestBody("text/plain".toMediaType()),
                password.toRequestBody("text/plain".toMediaType())
            )
            val user = response.data
            emit(AuthState.Success(user, response.message))
        } catch (e: HttpException) {
            val errorResult = parseHttpException(e)
            emit(errorResult)
        } catch (e: IOException) {
            emit(AuthState.Error("No Internet Connection"))
            Log.e(TAG, "Network Exception : ${e.localizedMessage}")
        } catch (e: Exception) {
            emit(AuthState.Error(e.message.toString()))
            Log.e(TAG, "Register : ${e.localizedMessage}")
        }
    }

    fun verifyEmail(email: String): Flow<Result<String>> = flow {
        emit(Result.Loading)
        try {
            val emailRequestBody = email.toRequestBody("text/plain".toMediaType())
            val response = apiService.verifyEmail(emailRequestBody)
            emit(Result.Success(response.message))

        } catch (e: HttpException) {
            val errorResult = parseHttpException(e)
//            emit(errorResult)
        } catch (e: IOException) {
            emit(Result.Error("No Internet Connection"))
            Log.e(TAG, "Network Exception: ${e.localizedMessage}")
        } catch (e: Exception) {
            Log.e(TAG, "Verify Email : ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    suspend fun saveUser(user: UserData) = authDataSource.saveUser(user)

    suspend fun logout() = authDataSource.logout()



    private fun parseHttpException(e: HttpException): AuthState.Error {
        val errorCode = e.code() // Retrieves the HTTP status code
        val jsonInString = e.response()?.errorBody()?.string()
        val errorBody = try {
            Gson().fromJson(jsonInString, ErrorResponse::class.java)
        } catch (ex: Exception) {
            null
        }

        val errorMessage = errorBody?.message ?: "Unknown error"
        Log.e(TAG, "HTTP Exception [$errorCode]: $errorMessage")
        return AuthState.Error(errorMessage)
    }


    companion object {
        const val TAG = "Auth Repository"

        @Volatile
        private var instance: AuthRepository? = null
        fun getInstance(
            apiService: ApiService, authDataSource: AuthDataSource
        ): AuthRepository = instance ?: synchronized(this) {
            instance ?: AuthRepository(apiService, authDataSource)
        }.also { instance = it }
    }
}
