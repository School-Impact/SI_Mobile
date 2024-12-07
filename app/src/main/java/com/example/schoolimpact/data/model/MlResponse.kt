package com.example.schoolimpact.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class MlResponse(

    @field:SerializedName("data") val mlResult: MlResultData,

    @field:SerializedName("message") val message: String
) : Parcelable

@Parcelize
data class MlResultData(

    @field:SerializedName("userId") val userId: Int,

    @field:SerializedName("majors") val majors: String,

    @field:SerializedName("interest") val interest: String,
) : Parcelable
