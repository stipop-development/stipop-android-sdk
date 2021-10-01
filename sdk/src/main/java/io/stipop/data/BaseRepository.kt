package io.stipop.data

import io.stipop.api.ApiResponse
import retrofit2.Response

open class BaseRepository {

    suspend fun <T : Any> safeCall(call: suspend () -> Response<T>): T {
        val result = getApiResponse(call)
        var output: T? = null
        when (result) {
            is ApiResponse.Success -> {
                output = result.output
            }
            is ApiResponse.Error -> {

            }
        }
        return output!!
    }

    private suspend fun <T : Any> getApiResponse(call: suspend () -> Response<T>): ApiResponse<T> {
        val response = call.invoke()
        return if (response.isSuccessful)
            ApiResponse.Success(response.body()!!)
        else
            ApiResponse.Error(Exception(""))
    }
}