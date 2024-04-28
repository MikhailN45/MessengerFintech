package com.study.messengerfintech.di.profile

import androidx.lifecycle.ViewModel
import com.study.messengerfintech.di.ViewModelKey
import com.study.messengerfintech.presentation.viewmodel.ProfileViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ProfileModule {

    @Binds
    @IntoMap
    @ViewModelKey(ProfileViewModel::class)
    abstract fun bindViewModel(viewModel: ProfileViewModel): ViewModel

}