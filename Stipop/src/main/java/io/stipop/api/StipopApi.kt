/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.stipop.api

import io.stipop.models.OrderChangeBody
import io.stipop.models.response.MyStickerOrderResponse
import io.stipop.models.response.MyStickerResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface StipopApi {

    @GET("mysticker/{userId}")
    suspend fun getMyStickers(
        @Header("apiKey") apiKey: String,
        @Path("userId") userId: String,
        @Query("pageNumber") pageNumber: Int,
        @Query("limit") limit: Int,
        @Query("platform") platform: String = "android-sdk",
    ): MyStickerResponse

    @GET("mysticker/hide/{userId}")
    suspend fun getMyHiddenStickers(
        @Header("apiKey") apiKey: String,
        @Path("userId") userId: String,
        @Query("pageNumber") pageNumber: Int,
        @Query("limit") limit: Int,
        @Query("platform") platform: String = "android-sdk",
    ): MyStickerResponse

    @PUT("mysticker/order/{userId}")
    suspend fun putMyStickerOrders(
        @Header("apiKey") apiKey: String,
        @Path("userId") userId: String,
        @Body orderChangeBody: OrderChangeBody,
        @Query("platform") platform: String = "android-sdk",
    ): MyStickerOrderResponse

    companion object {
        private const val BASE_URL = "https://messenger.stipop.io/v1/"
        fun create(): StipopApi {
            val logger = HttpLoggingInterceptor().apply { level = Level.BASIC }
            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(StipopApi::class.java)
        }
    }
}
