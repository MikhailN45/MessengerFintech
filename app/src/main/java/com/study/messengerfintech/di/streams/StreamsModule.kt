package com.study.messengerfintech.di.streams

import androidx.lifecycle.ViewModel
import com.study.messengerfintech.di.ViewModelKey
import com.study.messengerfintech.presentation.viewmodel.StreamsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class StreamsModule {

    @StreamsScope
    @Binds
    @IntoMap
    @ViewModelKey(StreamsViewModel::class)
    abstract fun bindViewModel(viewModel: StreamsViewModel): ViewModel
}