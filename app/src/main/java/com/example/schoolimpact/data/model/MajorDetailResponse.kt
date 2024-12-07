package com.example.schoolimpact.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MajorDetailResponse(
    val message: String, val data: List<MajorDetailItem>
) : Parcelable

@Parcelize
data class MajorDetailItem(
    val name: String, val description: String, val programs: List<Program>
) : Parcelable

@Parcelize
data class Program(
    val name: String, val competencies: List<Competency>
) : Parcelable

@Parcelize
data class Competency(
    val name: String
) : Parcelable


