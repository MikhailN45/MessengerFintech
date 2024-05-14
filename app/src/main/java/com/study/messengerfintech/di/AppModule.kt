package com.study.messengerfintech.di

import com.study.messengerfintech.data.repository.RepositoryImpl
import com.study.messengerfintech.domain.repository.Repository
import com.study.messengerfintech.domain.usecase.SearchTopicsUseCase
import com.study.messengerfintech.domain.usecase.SearchTopicsUseCaseImpl
import com.study.messengerfintech.domain.usecase.SearchUsersUseCase
import com.study.messengerfintech.domain.usecase.SearchUsersUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
interface AppModule {

    @Binds
    fun getSearchUsersUseCase(impl: SearchUsersUseCaseImpl): SearchUsersUseCase

    @Binds
    fun getSearchTopicsUseCase(impl: SearchTopicsUseCaseImpl): SearchTopicsUseCase

    @Binds
    fun getRepository(impl: RepositoryImpl): Repository

    companion object {
        @Singleton
        @Provides
        fun getJsonFormat(): Json = Json { ignoreUnknownKeys = true; coerceInputValues = true }
    }
}