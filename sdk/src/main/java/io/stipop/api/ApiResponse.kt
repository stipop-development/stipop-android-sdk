package io.stipop.api

sealed interface ApiResponse<out T> {
    data class Success<out T>(val output: T) : ApiResponse<T>
    data class Error(val code: Int? = null, val exception: Exception? = null) : ApiResponse<Nothing>
    object NetworkError: ApiResponse<Nothing>
}