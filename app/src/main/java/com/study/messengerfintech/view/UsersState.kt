package com.study.messengerfintech.view

import com.study.messengerfintech.model.data.User

sealed class UsersState {

    data class Success(val users: List<User>) : UsersState()

    data object Loading : UsersState()

    class Error(val error: Throwable) : UsersState()
}