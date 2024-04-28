package com.study.messengerfintech.di.chat

import androidx.lifecycle.ViewModel
import com.study.messengerfintech.di.ViewModelKey
import com.study.messengerfintech.presentation.viewmodel.ChatViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ChatModule {

    @Binds
    @IntoMap
    @ViewModelKey(ChatViewModel::class)
    abstract fun bindViewModel(viewmodel: ChatViewModel): ViewModel

}
