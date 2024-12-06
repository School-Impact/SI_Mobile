package com.example.schoolimpact.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class RegisterResponse(

    @field:SerializedName("message") val message: String,

    @field:SerializedName("error") val error: Boolean? = false,

    val data: UserData? = null
)

@Parcelize
data class LoginResponse(

    @field:SerializedName("message") val message: String? = null,

    @field:SerializedName("data") val data: UserData? = null,

    @field:SerializedName("token") val token: String? = null
) : Parcelable

@Parcelize
data class UserData(

    @field:SerializedName("name") val name: String? = null,

    @field:SerializedName("email") val email: String? = null,

    @field:SerializedName("education") val education: String? = null,

    @field:SerializedName("image") val image: String? = null,

    @field:SerializedName("phone_number") val phoneNumber: String? = null,

    val token: String = ""

) : Parcelable

@Parcelize
data class EmailResponse(
    @field:SerializedName("message") val message: String
) : Parcelable


data class ErrorResponse(
    @field:SerializedName("error") val error: Boolean? = null,
    @field:SerializedName("message") val message: String? = null
)

