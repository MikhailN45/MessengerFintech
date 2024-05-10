package com.study.messengerfintech.di.streams

import com.study.messengerfintech.di.main.MainModule
import com.study.messengerfintech.presentation.fragments.StreamsFragment
import dagger.Subcomponent

@StreamsScope
@Subcomponent(
    modules =
    [MainModule::class]
)
abstract class StreamsComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): StreamsComponent
    }

    abstract fun inject(fragment: StreamsFragment)
}