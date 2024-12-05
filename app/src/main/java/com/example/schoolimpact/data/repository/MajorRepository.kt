package com.example.schoolimpact.data.repository

import android.nfc.Tag
import android.util.Log
import com.example.schoolimpact.data.api.ApiService
import com.example.schoolimpact.data.model.ErrorResponse
import com.example.schoolimpact.data.model.MajorListItem
import com.example.schoolimpact.utils.Result
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException


class MajorRepository private constructor(
    private val apiService: ApiService
) {


    fun getMajors(category: String): Flow<Result<List<MajorListItem>>> = flow {
        emit(Result.Loading)
        try {
            val token = "Bearer"
//            val token = "Bearer ${getToken()}"
            val response = apiService.getMajorList(category, token).data
            emit(Result.Success(response))

        } catch (e: HttpException) {
            val errorResult = parseHttpException(e)
            emit(errorResult)
        } catch (e: IOException) {
            emit(Result.Error("No Internet Connection"))
            Log.e(TAG, "Network Exception: ${e.localizedMessage}")
        } catch (e: Exception) {
            Log.e(TAG, "Verify Email : ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
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

        @Volatile
        private var instance: MajorRepository? = null
        fun getInstance(
            apiService: ApiService
        ): MajorRepository = instance ?: synchronized(this) {
            instance ?: MajorRepository(apiService)
        }.also { instance = it }
    }

}