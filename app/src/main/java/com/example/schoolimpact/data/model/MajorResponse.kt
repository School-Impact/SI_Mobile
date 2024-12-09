package com.example.schoolimpact.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class MajorResponse(

    @field:SerializedName("data")
    val data: List<ListMajorItem>,

    @field:SerializedName("message")
    val message: String? = null
) : Parcelable

@Parcelize
data class ListMajorItem(

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("description")
    val description: String? = null
) : Parcelable
