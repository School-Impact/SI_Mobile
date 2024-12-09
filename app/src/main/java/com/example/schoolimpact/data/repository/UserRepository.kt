package com.example.schoolimpact.data.repository

import android.util.Log
import com.example.schoolimpact.data.api.ApiService
import com.example.schoolimpact.data.model.ErrorResponse
import com.example.schoolimpact.data.model.ProfileData
import com.example.schoolimpact.data.model.UpdateProfileResponse
import com.example.schoolimpact.data.preferences.AuthDataSource
import com.example.schoolimpact.data.repository.AuthRepository.Companion.TAG
import com.example.schoolimpact.utils.Result
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val apiService: ApiService, private val authDataSource: AuthDataSource
) {


    fun getUserProfile(): Flow<Result<ProfileData>> = flow {
        emit(Result.Loading)
        try {
            val token = getToken() ?: throw Exception("Token not found")
            val bearerToken = "Bearer $token"
            val response = apiService.getUserProfile(bearerToken).data
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val errorResult = parseHttpException(e)
            emit(errorResult)
        } catch (e: IOException) {
            emit(Result.Error("No Internet Connection"))
            Log.e(TAG, "Network Exception : ${e.localizedMessage}")
        } catch (e: Exception) {
            Log.e(TAG, "Login : ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun updateUserProfile(
        name: String,
        education: String,
        imageFile: File,
        phoneNumber: String,
        password: String
    ): Flow<Result<UpdateProfileResponse>> = flow {
        emit(Result.Loading)
        try {
            val token = getToken() ?: throw Exception("Token not found")
            val bearerToken = "Bearer $token"
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo", imageFile.name, requestImageFile
            )
            val response = apiService.updateUserProfile(
                bearerToken,
                name.toRequestBody("text/plain".toMediaType()),
                education.toRequestBody("text/plain".toMediaType()),
                multipartBody,
                phoneNumber.toRequestBody("text/plain".toMediaType()),
                password.toRequestBody("text/plain".toMediaType())
            )
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val errorResult = parseHttpException(e)
            emit(errorResult)
        } catch (e: IOException) {
            emit(Result.Error("No Internet Connection"))
            Log.e(TAG, "Network Exception : ${e.localizedMessage}")
        } catch (e: Exception) {
            Log.e(TAG, "Update profile : ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }


    suspend fun logout() = authDataSource.logout()

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
        Log.e(MajorRepository.TAG, "HTTP Exception [$errorCode]: $errorMessage")
        return Result.Error(errorMessage)
    }
}