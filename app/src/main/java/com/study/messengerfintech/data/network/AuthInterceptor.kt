package com.study.messengerfintech.data.network

import android.util.Log
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

class AuthInterceptor : Interceptor {
    private val auth =
        Credentials.basic(
            "shelove6391@gmail.com",
            "Ah3uZ10CjoZOFvzd9HfOvaW3deZZEOt9"
        )

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val authenticatedRequest: Request =
            request
                .newBuilder()
                .header("Authorization", auth)
                .build()
        try {
            return chain.proceed(authenticatedRequest)
        } catch (e: Exception) {
            Log.d("AuthInterceptor", "${e.message}")
            e.printStackTrace()
            return Response.Builder()
                .code(504)
                .message("Network Timeout Error")
                .protocol(Protocol.HTTP_1_1)
                .request(chain.request())
                .body("".toResponseBody("text/plain".toMediaTypeOrNull()))
                .build()
        }
    }
}