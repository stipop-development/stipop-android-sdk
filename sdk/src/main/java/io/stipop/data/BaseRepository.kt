package io.stipop.data

import android.util.Log
import io.stipop.api.ApiResponse
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEmpty
import retrofit2.HttpException
import java.io.IOException
import java.net.UnknownHostException

internal open class BaseRepository {

    suspend fun <T : Any> safeCall(
        call: suspend () -> T,
        onSuccess: (data: T) -> Unit,
        onError: (message: String?) -> Unit
    ) {
        try {
            onSuccess(call.invoke())
        } catch (e: Exception) {
            onError(e.message)
        }
    }

    suspend fun <T : Any> safeCallAsFlow(
        call: suspend () -> T
    ): Flow<T?> {
        return try{
            val r = call.invoke()
            flowOf(r)
        }catch (e:Exception){
            flowOf(null)
        }
    }

    suspend fun <T : Any> safeCall(
        call: suspend () -> T,
        onCompletable: (data: T?) -> Unit,
    ) {
        try {
            val result = call.invoke()
            onCompletable(result)
        } catch (e: Exception) {
            onCompletable(null)
        }
    }

    suspend fun <T> safeApiCall(apiCall: suspend () -> T): ApiResponse<T> {
        return try {
            ApiResponse.Success(apiCall.invoke())
        } catch (throwable: Throwable) {
            Log.e("STIPOP-API", "${throwable.cause} :: ${throwable.localizedMessage}")
            when (throwable) {
                is IOException -> ApiResponse.NetworkError
                is HttpException -> {
                    val code = throwable.code()
                    ApiResponse.Error(code, Exception(throwable.localizedMessage))
                }
                is UnknownHostException -> {
                    ApiResponse.Error(null, Exception(throwable.localizedMessage))
                }
                else -> {
                    ApiResponse.Error(null, Exception(throwable.localizedMessage))
                }
            }
        }
    }
}