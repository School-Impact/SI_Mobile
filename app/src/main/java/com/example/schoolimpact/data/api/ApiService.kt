package com.example.schoolimpact.data.api

import com.example.schoolimpact.data.model.EmailResponse
import com.example.schoolimpact.data.model.LoginResponse
import com.example.schoolimpact.data.model.RegisterResponse
import com.example.schoolimpact.utils.Result
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Headers("Accept: application/json")
    @Multipart
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("education_level") educationLevel: String,
        @Field("password") password: String
    ): RegisterResponse

    @Multipart
    @POST("login")
    suspend fun login(
        @Field("email") email: String, @Field("password") password: String,
    ): LoginResponse

    @POST("auth/register")
    @Multipart
    suspend fun verifyEmail(
        @Part("email") email: RequestBody
    ): EmailResponse

}