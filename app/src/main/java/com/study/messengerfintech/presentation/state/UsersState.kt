package com.study.messengerfintech.presentation.state

sealed interface UsersState {
    data object Success : UsersState
    data object Loading : UsersState
    class Error(val error: Throwable) : UsersState
}