package com.study.messengerfintech.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.study.messengerfintech.data.network.AuthInterceptor
import com.study.messengerfintech.data.network.ZulipApiService
import dagger.Module
import dagger.Provides
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import javax.inject.Singleton

@Module
class RetrofitModule {
    @Provides
    fun provideOkHttpClient() = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .addInterceptor(AuthInterceptor())
        .build()

    @Singleton
    @Provides
    @ExperimentalSerializationApi
    fun provideRetrofit(client: OkHttpClient, json: Json, urlProvider: UrlProvider): Retrofit =
        Retrofit.Builder()
            .baseUrl(urlProvider.apiUrl)
            .client(client)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    @Singleton
    @Provides
    fun provideService(retrofit: Retrofit): ZulipApiService =
        retrofit.create(ZulipApiService::class.java)
}