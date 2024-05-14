package com.study.messengerfintech.di.profile

import com.study.messengerfintech.presentation.fragments.ProfileFragment
import dagger.Subcomponent

@Subcomponent(
    modules =
    [ProfileModule::class]
)
abstract class ProfileComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): ProfileComponent
    }

    abstract fun inject(fragment: ProfileFragment)
}