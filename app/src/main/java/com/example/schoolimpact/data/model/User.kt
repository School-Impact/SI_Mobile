package com.example.schoolimpact.data.model

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class User(
    val name: String,
    val token: String
)
