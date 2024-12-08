package com.example.schoolimpact.data.repository

import android.util.Log
import com.example.schoolimpact.data.api.ApiService
import com.example.schoolimpact.data.model.ErrorResponse
import com.example.schoolimpact.data.model.InterestRequest
import com.example.schoolimpact.data.model.MlResponse
import com.example.schoolimpact.data.preferences.AuthDataSource
import com.example.schoolimpact.ui.main.recommendation.MlResult
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class MlRepository @Inject constructor(
    private val apiService: ApiService, private val authDataSource: AuthDataSource
) {
    fun postInterest(interest: String): Flow<MlResult<MlResponse>> = flow {
        emit(MlResult.Loading)
        try {
            val token = getToken() ?: throw Exception("Token not found")
            val bearerToken = "Bearer $token"
            val interestRequest = InterestRequest(interest)
            Log.d("API Request", "Sending: Token=$bearerToken, Interest=$interestRequest")
            val response = apiService.postInterest(bearerToken, interestRequest)
            emit(MlResult.Success(response, response.message))
        } catch (e: HttpException) {
            val errorResult = parseHttpException(e)
            emit(errorResult)
        } catch (e: IOException) {
            emit(MlResult.Error("No Internet Connection"))
            Log.e(TAG, "Network Exception: ${e.localizedMessage}")
        } catch (e: Exception) {
            Log.e(TAG, "ML : ${e.message.toString()}")
            emit(MlResult.Error(e.message.toString()))
        }
    }

    private suspend fun getToken(): String? {
        val token = authDataSource.user.firstOrNull()?.token
        Log.d("MlRepository", "Retrieved Token: $token")
        return token
    }

    private fun parseHttpException(e: HttpException): MlResult.Error {
        val errorCode = e.code() // Retrieves the HTTP status code
        val jsonInString = e.response()?.errorBody()?.string()
        val errorBody = try {
            Gson().fromJson(jsonInString, ErrorResponse::class.java)
        } catch (ex: Exception) {
            null
        }

        val errorMessage = errorBody?.message ?: "Unknown error"
        Log.e(TAG, "HTTP Exception [$errorCode]: $errorMessage")
        return MlResult.Error(errorMessage)
    }

    companion object {
        const val TAG = "Ml Repository"
    }
}