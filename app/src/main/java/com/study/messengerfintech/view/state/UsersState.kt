package com.study.messengerfintech.view.state

sealed class UsersState {
    data object Success : UsersState()
    data object Loading : UsersState()
    class Error(val error: Throwable) : UsersState()
}