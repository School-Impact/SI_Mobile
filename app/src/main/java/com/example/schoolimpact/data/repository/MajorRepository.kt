package com.example.schoolimpact.data.repository

import android.util.Log
import com.example.schoolimpact.data.api.ApiService
import com.example.schoolimpact.data.model.ErrorResponse
import com.example.schoolimpact.data.model.ListMajorItem
import com.example.schoolimpact.data.model.MajorDetailItem
import com.example.schoolimpact.data.preferences.AuthDataSource
import com.example.schoolimpact.utils.Result
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


class MajorRepository @Inject constructor(
    private val apiService: ApiService, private val authDataSource: AuthDataSource
) {

    fun getMajors(category: String): Flow<Result<List<ListMajorItem>>> = flow {
        emit(Result.Loading)
        try {
            val token = getToken() ?: throw Exception("Token not found")
            val bearerToken = "Bearer $token"
            val response = apiService.getMajorList(bearerToken, category).data
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

    fun getMajorDetail(id: Int): Flow<Result<List<MajorDetailItem>>> = flow {
        emit(Result.Loading)
        try {
            val token = getToken() ?: throw Exception("Token not found")
            val bearerToken = "Bearer $token"
            val response = apiService.getMajorDetail(bearerToken, id).data
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val errorResult = parseHttpException(e)
            emit(errorResult)
        } catch (e: IOException) {
            emit(Result.Error("No Internet Connection"))
            Log.e(TAG, "Network Exception: ${e.localizedMessage}")
        } catch (e: Exception) {
            Log.e(TAG, "Major Detail : ${e.message.toString()}")
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
        const val TAG = "Major Repository"
    }

}