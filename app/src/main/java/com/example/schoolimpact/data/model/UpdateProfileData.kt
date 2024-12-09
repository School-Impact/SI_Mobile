package com.example.schoolimpact.data.model

import com.google.gson.annotations.SerializedName

data class UpdateProfileResponse(

    @field:SerializedName("data")
    val data: UpdateProfileData,

    @field:SerializedName("message")
    val message: String
)

data class UpdateProfileData(

    @field:SerializedName("image")
    val image: String,

    @field:SerializedName("password")
    val password: String,

    @field:SerializedName("education")
    val education: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("phone_number")
    val phoneNumber: String
)
