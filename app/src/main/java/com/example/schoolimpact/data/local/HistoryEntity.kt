package com.example.schoolimpact.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val majorId: Int,
    val majors: String,
    val interest: String,
    val timestamp: Long = System.currentTimeMillis()
)