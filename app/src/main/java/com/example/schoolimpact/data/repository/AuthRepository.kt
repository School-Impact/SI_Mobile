package com.example.schoolimpact.data.repository

import android.util.Log
import com.example.schoolimpact.data.api.ApiService
import com.example.schoolimpact.data.model.ErrorResponse
import com.example.schoolimpact.data.model.User
import com.example.schoolimpact.data.preferences.AuthDataSource
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

    fun login(email: String, password: String): Flow<Result<User>> = flow {
        emit(Result.Loading)
        try {
            val emailRequestBody = email.toRequestBody("text/plain".toMediaType())
            val passwordRequestBody = password.toRequestBody("text/plain".toMediaType())
            val response = apiService.login(emailRequestBody, passwordRequestBody)
            val userData = response.data
            val user = User(
                name = userData?.name.toString(), token = response.token.toString()
            )
            emit(Result.Success(user))
        } catch (e: HttpException) {
            val errorResult = parseHttpException(e, TAG)
            emit(errorResult)
        } catch (e: IOException) {
            emit(Result.Error("No Internet Connection"))
            Log.e(TAG, "Network Exception : ${e.localizedMessage}")
        } catch (e: Exception) {
            Log.e(TAG, "Login : ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun register(
        name: String, email: String, educationLevel: String, phoneNumber: String, password: String
    ): Flow<Result<String>> = flow {
        emit(Result.Loading)
        try {
            val nameRequestBody = name.toRequestBody("text/plain".toMediaType())
            val emailRequestBody = email.toRequestBody("text/plain".toMediaType())
            val educationRequestBody = educationLevel.toRequestBody("text/plain".toMediaType())
            val phoneNumberRequestBody = phoneNumber.toRequestBody("text/plain".toMediaType())
            val passwordRequestBody = password.toRequestBody("text/plain".toMediaType())
            val response = apiService.register(
                nameRequestBody,
                emailRequestBody,
                educationRequestBody,
                phoneNumberRequestBody,
                passwordRequestBody
            ).message
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val errorResult = parseHttpException(e, TAG)
            emit(errorResult)
        } catch (e: IOException) {
            emit(Result.Error("No Internet Connection"))
            Log.e(TAG, "Network Exception : ${e.localizedMessage}")
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
            Log.e(TAG, "Register : ${e.localizedMessage}")
        }
    }

    suspend fun saveUser(user: User) = authDataSource.saveUser(user)

    suspend fun logout() = authDataSource.logout()


    fun verifyEmail(email: String): Flow<Result<String>> = flow {
        emit(Result.Loading)
        try {
            val emailRequestBody = email.toRequestBody("text/plain".toMediaType())
            val response = apiService.verifyEmail(emailRequestBody)
            emit(Result.Success(response.message))

        } catch (e: HttpException) {
            val errorResult = parseHttpException(e, TAG)
            emit(errorResult)
        } catch (e: IOException) {
            emit(Result.Error("No Internet Connection"))
            Log.e(TAG, "Network Exception: ${e.localizedMessage}")
        } catch (e: Exception) {
            Log.e(TAG, "Verify Email : ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    private fun parseHttpException(e: HttpException, tag: String): Result.Error {
        val errorCode = e.code() // Retrieves the HTTP status code
        val jsonInString = e.response()?.errorBody()?.string()
        val errorBody = try {
            Gson().fromJson(jsonInString, ErrorResponse::class.java)
        } catch (ex: Exception) {
            null
        }

        val errorMessage = errorBody?.message ?: "Unknown error"
        Log.e(tag, "HTTP Exception [$errorCode]: $errorMessage")
        return Result.Error(errorMessage)
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
