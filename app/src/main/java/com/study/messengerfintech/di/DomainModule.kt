package com.study.messengerfintech.di

import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface DomainModule {
    @Singleton
    @Binds
    fun bindUrlProvider(impl: UrlProvider.UrlProviderImpl): UrlProvider
}