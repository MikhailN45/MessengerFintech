package com.study.messengerfintech.di

import com.study.messengerfintech.data.repository.ChatRepositoryImpl
import com.study.messengerfintech.data.repository.StreamRepositoryImpl
import com.study.messengerfintech.data.repository.UserRepositoryImpl
import com.study.messengerfintech.domain.repository.ChatRepository
import com.study.messengerfintech.domain.repository.StreamRepository
import com.study.messengerfintech.domain.repository.UserRepository
import com.study.messengerfintech.domain.usecase.SearchTopicsUseCase
import com.study.messengerfintech.domain.usecase.SearchTopicsUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
interface AppModule {

    @Binds
    fun getSearchTopicsUseCase(impl: SearchTopicsUseCaseImpl): SearchTopicsUseCase

    @Binds
    fun getStreamRepository(impl: StreamRepositoryImpl): StreamRepository

    @Binds
    fun getUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    fun getChatRepository(impl: ChatRepositoryImpl): ChatRepository

    companion object {
        @Singleton
        @Provides
        fun getJsonFormat(): Json = Json { ignoreUnknownKeys = true; coerceInputValues = true }
    }
}