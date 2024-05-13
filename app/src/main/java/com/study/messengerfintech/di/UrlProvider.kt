package com.study.messengerfintech.di

import javax.inject.Inject

interface UrlProvider {
    var apiUrl: String

    class UrlProviderImpl @Inject constructor(): UrlProvider {
        override var apiUrl: String = API_URL
    }

    companion object {
        const val API_URL = "https://tinkoff-android-spring-2024.zulipchat.com/api/v1/"
    }
}