package io.stipop.data

import android.util.Log
import io.stipop.api.ApiResponse
import io.stipop.api.StipopApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import retrofit2.HttpException
import java.io.IOException
import java.net.UnknownHostException

internal open class BaseRepository {
    /*
    suspend fun <T : Any> safeCall(
        call: suspend () -> T,
        onSuccess: (data: T) -> Unit,
        onError: (message: String?) -> Unit
    ) {
        try {
            onSuccess(call.invoke())
        } catch (exception: Exception) {
            onError(exception.message)
        }
    }
     */

    suspend fun <T : Any> safeCallAsFlow(
        retryCount: Int = 1,
        call: suspend () -> T
    ): Flow<T?> {
        try{
            if(retryCount <= 3) {
                val r = call.invoke()
                return flowOf(r)
            } else {
                return flowOf(null)
            }
        }catch(exception: HttpException){
            var result: Flow<T?> = flowOf(null)
            when(exception.code()) {
                // 400 : Bad request.
                400 -> {}
                // 401 : Unauthorized.
                401 -> {
                    SAuthRepository.getAccessToken()

                    var retryCountNumber = retryCount
                    retryCountNumber++
                    result = safeCallAsFlow(retryCount = retryCountNumber, call = call)
                }
            }
            return result
        }catch (exception:Exception){
            return flowOf(null)
        }
    }


//    fun <T : Any> safeCallAsGeneric(
//        call: suspend () -> T
//    ): T? {
//        return try{
//            val r = call
//            return r
//        }catch (exception:Exception){
//            return null
//        }
//    }

    suspend fun <T : Any> safeCall(
        retryCount: Int = 1,
        call: suspend () -> T,
        onCompletable: (data: T?) -> Unit,
    ) {
        try {
            if(retryCount <= 3) {
                val result = call.invoke()
                onCompletable(result)
            } else {
                onCompletable(null)
            }
        }catch(exception: HttpException){
            when(exception.code()){
                // 400 : Bad request.
                400 -> {}
                // 401 : Unauthorized.
                401 -> {
                    SAuthRepository.getAccessToken()

                    var retryCountNumber = retryCount
                    retryCountNumber++
                    safeCall(retryCount = retryCountNumber, call = call, onCompletable = onCompletable)
                }
            }
        } catch (exception: Exception) {
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