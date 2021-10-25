package io.stipop.data

import android.util.Log
import io.stipop.api.ApiResponse
import retrofit2.Response
import java.net.UnknownHostException

open class BaseRepository {

    suspend fun <T : Any> safeCall(call: suspend () -> Response<T>): T? {
        val result = getApiResponse(call)
        Log.d("STIPOP-DEBUG", "$result")
        var output: T? = null
        when (result) {
            is ApiResponse.Success -> {
                output = result.output
            }
            is ApiResponse.Error -> {
                Log.e("STIPOP-SDK", "${result.exception}")
            }
        }
        return output
    }

    private suspend fun <T : Any> getApiResponse(call: suspend () -> Response<T>): ApiResponse<T> {
        try {
            val response = call.invoke()
            return if (response.isSuccessful)
                ApiResponse.Success(response.body()!!)
            else
                ApiResponse.Error(Exception(response.errorBody().toString()))
        }catch (e: UnknownHostException){
            return ApiResponse.Error(UnknownHostException())
        }
    }
}