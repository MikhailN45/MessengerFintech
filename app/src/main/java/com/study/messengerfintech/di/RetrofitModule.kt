package com.study.messengerfintech.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.study.messengerfintech.data.network.AuthInterceptor
import com.study.messengerfintech.data.network.ZulipRetrofitApi
import dagger.Module
import dagger.Provides
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
class RetrofitModule {

    @Provides
    @Named("URL")
    fun provideURL() = API_URL

    @Provides
    fun provideOkHttpClient() = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .addInterceptor(AuthInterceptor())
        .build()

    @Singleton
    @Provides
    @ExperimentalSerializationApi
    fun provideRetrofit(@Named("URL") url: String, client: OkHttpClient, json: Json): Retrofit =
        Retrofit.Builder()
            .baseUrl(url)
            .client(client)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    @Singleton
    @Provides
    fun provideService(retrofit: Retrofit): ZulipRetrofitApi =
        retrofit.create(ZulipRetrofitApi::class.java)

    companion object {
        const val API_URL = "https://tinkoff-android-spring-2024.zulipchat.com/api/v1/"
    }
}