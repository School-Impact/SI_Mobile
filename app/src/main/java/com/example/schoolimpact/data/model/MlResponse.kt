package com.example.schoolimpact.data.model

import com.google.gson.annotations.SerializedName

data class MlResponse(

    @field:SerializedName("data") val mlResult: MlResultData,

    @field:SerializedName("message") val message: String
)

data class MlResultData(

    @field:SerializedName("userId") val userId: Int,

    @field:SerializedName("majorId") val majorId: Int,

    @field:SerializedName("majors") val majors: String,

    @field:SerializedName("interest") val interest: String,
)

data class InterestRequest(
    val interest: String
)
