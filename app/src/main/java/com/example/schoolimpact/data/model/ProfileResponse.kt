package com.example.schoolimpact.data.model

import com.google.gson.annotations.SerializedName

data class ProfileResponse(

    @field:SerializedName("data")
    val data: ProfileData,

    @field:SerializedName("message")
    val message: String
)

data class ProfileData(

    @field:SerializedName("image")
    val image: String,

    @field:SerializedName("password")
    val password: String,

    @field:SerializedName("education")
    val education: String,

    @field:SerializedName("updated_at")
    val updatedAt: Any,

    @field:SerializedName("verified_at")
    val verifiedAt: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("created_at")
    val createdAt: String,

    @field:SerializedName("phone_number")
    val phoneNumber: String,

    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("remember_token")
    val rememberToken: String,

    @field:SerializedName("email")
    val email: String
)
