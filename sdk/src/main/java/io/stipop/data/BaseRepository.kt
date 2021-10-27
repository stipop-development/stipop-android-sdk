package io.stipop.data

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

internal open class BaseRepository {

    suspend fun <T : Any> safeCall(call: suspend () -> Call<T>, onCompletable: (data: T?) -> Unit) {
        call.invoke().enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                onCompletable(response.body())
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                Log.e("STIPOP-SDK", "$t : $call")
                onCompletable(null)
            }
        })
    }

    suspend fun <T : Any> safeCall(
        call: suspend () -> Call<T>,
        onSuccess: (data: T) -> Unit,
        onFail: (message: String) -> Unit
    ) {
        call.invoke().enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!)
                } else {
                    onFail(response.message())
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                onFail("${t.message}/${t.localizedMessage}")
            }
        })
    }
}