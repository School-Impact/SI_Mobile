package com.example.schoolimpact.data.api

import com.example.schoolimpact.data.model.EmailResponse
import com.example.schoolimpact.data.model.InterestRequest
import com.example.schoolimpact.data.model.LoginResponse
import com.example.schoolimpact.data.model.MajorDetailResponse
import com.example.schoolimpact.data.model.MajorResponse
import com.example.schoolimpact.data.model.MlResponse
import com.example.schoolimpact.data.model.RegisterResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @Headers("Accept: application/json")
    @Multipart
    @POST("auth/dataRegister")
    suspend fun register(
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("education") educationLevel: RequestBody,
        @Part("phone_number") phoneNumber: RequestBody,
        @Part("password") password: RequestBody,
    ): RegisterResponse

    @Multipart
    @POST("auth/login")
    suspend fun login(
        @Part("email") email: RequestBody, @Part("password") password: RequestBody,
    ): LoginResponse

    @POST("auth/register")
    @Multipart
    suspend fun verifyEmail(
        @Part("email") email: RequestBody
    ): EmailResponse

    @GET("user/majors")
    suspend fun getMajorList(
        @Header("Authorization") token: String, @Query("category") category: String
    ): MajorResponse

    @GET("user/majorsDetail/{id}")
    suspend fun getMajorDetail(
        @Header("Authorization") token: String, @Path("id") id: Int
    ): MajorDetailResponse


    @POST("user/predict")
    suspend fun postInterest(
        @Header("Authorization") token: String,
        @Body interest: InterestRequest
    ): MlResponse

}