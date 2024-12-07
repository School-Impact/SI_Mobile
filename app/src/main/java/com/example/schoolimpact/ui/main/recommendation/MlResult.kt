package com.example.schoolimpact.ui.main.recommendation

sealed class MlResult<out T> {
    data object Initial : MlResult<Nothing>()
    data object Loading : MlResult<Nothing>()
    data class Success<out T>(val data: T, val message: String) : MlResult<T>()
    data class Error(val error: String) : MlResult<Nothing>()
}