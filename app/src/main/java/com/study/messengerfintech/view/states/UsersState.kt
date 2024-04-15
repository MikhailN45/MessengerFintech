package com.study.messengerfintech.view.states

import com.study.messengerfintech.model.data.User

sealed class UsersState {

    data class Success(val users: List<User>) : UsersState()

    data object Loading : UsersState()

    data object Error : UsersState()
}