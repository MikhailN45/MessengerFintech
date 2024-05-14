package com.study.messengerfintech.di.users

import com.study.messengerfintech.presentation.fragments.UsersFragment
import dagger.Subcomponent

@UsersScope
@Subcomponent(
    modules =
    [UserModule::class]
)
abstract class UserComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): UserComponent
    }

    abstract fun inject(fragment: UsersFragment)
}