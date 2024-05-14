package com.study.messengerfintech.di.users

import androidx.lifecycle.ViewModel
import com.study.messengerfintech.di.ViewModelKey
import com.study.messengerfintech.domain.usecase.SearchUsersUseCase
import com.study.messengerfintech.domain.usecase.SearchUsersUseCaseImpl
import com.study.messengerfintech.presentation.viewmodel.UsersViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class UserModule {

    @UsersScope
    @Binds
    abstract fun getSearchUsersUseCase(impl: SearchUsersUseCaseImpl): SearchUsersUseCase

    @UsersScope
    @Binds
    @IntoMap
    @ViewModelKey(UsersViewModel::class)
    abstract fun bindViewModel(viewModel: UsersViewModel): ViewModel
}