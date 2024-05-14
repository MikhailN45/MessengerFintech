package com.study.messengerfintech.di.streamstopics

import com.study.messengerfintech.di.main.MainModule
import com.study.messengerfintech.presentation.fragments.StreamsTopicsListFragment
import dagger.Subcomponent

@Subcomponent(
    modules =
    [MainModule::class]
)
abstract class StreamsTopicsComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): StreamsTopicsComponent
    }

    abstract fun inject(fragment: StreamsTopicsListFragment)
}