package com.example.schoolimpact.data.repository

import android.util.Log
import com.example.schoolimpact.data.api.ApiService
import com.example.schoolimpact.data.model.ErrorResponse
import com.example.schoolimpact.data.model.MlResponse
import com.example.schoolimpact.data.preferences.AuthDataSource
import com.example.schoolimpact.utils.Result
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class MlRepository @Inject constructor(
    private val apiService: ApiService, private val authDataSource: AuthDataSource
) {
    fun postInterest(interest: String): Flow<Result<MlResponse>> = flow {
        emit(Result.Loading)
        try {
            val token = getToken() ?: throw Exception("Token not found")
            val bearerToken = "Bearer $token"
            val interestRequestBody = interest.toRequestBody("text/plain".toMediaType())
            val response = apiService.postInterest(bearerToken, interestRequestBody)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val errorResult = parseHttpException(e)
            emit(errorResult)
        } catch (e: IOException) {
            emit(Result.Error("No Internet Connection"))
            Log.e(TAG, "Network Exception: ${e.localizedMessage}")
        } catch (e: Exception) {
            Log.e(TAG, "Major : ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    private suspend fun getToken(): String? {
        val token = authDataSource.user.firstOrNull()?.token
        Log.d("MajorRepository", "Retrieved Token: $token")
        return token
    }

    private fun parseHttpException(e: HttpException): Result.Error {
        val errorCode = e.code() // Retrieves the HTTP status code
        val jsonInString = e.response()?.errorBody()?.string()
        val errorBody = try {
            Gson().fromJson(jsonInString, ErrorResponse::class.java)
        } catch (ex: Exception) {
            null
        }

        val errorMessage = errorBody?.message ?: "Unknown error"
        Log.e(TAG, "HTTP Exception [$errorCode]: $errorMessage")
        return Result.Error(errorMessage)
    }

    companion object {
        const val TAG = "Ml Repository"

        @Volatile
        private var instance: MajorRepository? = null
        fun getInstance(
            apiService: ApiService, authDataSource: AuthDataSource
        ): MajorRepository = instance ?: synchronized(this) {
            instance ?: MajorRepository(apiService, authDataSource)
        }.also { instance = it }
    }
}