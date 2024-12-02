package com.example.schoolimpact.data.api

import com.example.schoolimpact.data.model.LoginResponse
import com.example.schoolimpact.data.model.RegisterResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("education_level") educationLevel: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String, @Field("password") password: String
    ): LoginResponse

}