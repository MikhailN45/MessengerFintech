package com.study.messengerfintech.di.main

import com.study.messengerfintech.presentation.MainActivity
import dagger.Subcomponent

@Subcomponent(
    modules =
    [MainModule::class]
)
abstract class MainComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): MainComponent
    }

    abstract fun inject(activity: MainActivity)

}