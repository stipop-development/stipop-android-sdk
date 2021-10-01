package io.stipop.api

sealed interface ApiResponse<out T : Any> {
    data class Success<out T : Any>(val output: T) : ApiResponse<T>
    data class Error(val exception: Exception) : ApiResponse<Nothing>
}