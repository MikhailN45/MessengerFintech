package com.study.messengerfintech.model.network

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

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
        return chain.proceed(authenticatedRequest)
    }
}