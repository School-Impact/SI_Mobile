package com.example.schoolimpact.data.repository

import android.util.Log
import com.example.schoolimpact.data.api.ApiService
import com.example.schoolimpact.data.model.EmailResponse
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
            val response = apiService.login(email, password)
            val userData = response.data
            val user = User(
                name = userData?.name.toString(),
                token = response.token.toString()
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

    //untuk testing
//    fun login(email: String, password: String): Flow<Result<User>> = flow {
//        emit(Result.Loading)
//        try {
//            // Simulate network delay
//            delay(1000)
//
//            // Dummy User for testing
//            val dummyEmail = "test@example.com"
//            val dummyPassword = "password123"
//
//            if (email == dummyEmail && password == dummyPassword) {
//                val dummyUser = User(
//                    userId = "12345",
//                    name = "Test User",
//                    token = "dummy_token_12345"
//                )
//                emit(Result.Success(dummyUser))
//            } else {
//                emit(Result.Error("Invalid credentials"))
//            }
//        } catch (e: Exception) {
//            emit(Result.Error(e.message.toString()))
//            Log.e(TAG, "Login : ${e.message.toString()}")
//        }
//    }

    fun register(
        name: String, email: String, educationLevel: String, password: String
    ): Flow<Result<String>> = flow {
        emit(Result.Loading)
        try {
            val message = apiService.register(name, email, educationLevel, password).message
            emit(Result.Success(message))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            emit(Result.Error(errorMessage.toString()))
            Log.e(TAG, "Register : $errorMessage")
        } catch (e: IOException) {
            emit(Result.Error("No Internet Connection"))
            Log.e(TAG, "Register : ${e.localizedMessage}")
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
            Log.e(TAG, "Register : ${e.localizedMessage}")
        }
    }

    suspend fun saveUser(user: User) = authDataSource.saveUser(user)

    suspend fun logout() = authDataSource.logout()

    suspend fun verifyEmail(email: String): EmailResponse =
        apiService.verifyEmail(email.toRequestBody("text/plain".toMediaType()))


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
