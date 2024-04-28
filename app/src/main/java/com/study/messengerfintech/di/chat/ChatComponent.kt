package com.study.messengerfintech.di.chat

import com.study.messengerfintech.presentation.fragments.ChatFragment
import dagger.Subcomponent

@Subcomponent(
    modules =
    [ChatModule::class]
)
abstract class ChatComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): ChatComponent
    }

    abstract fun inject(fragment: ChatFragment)

}