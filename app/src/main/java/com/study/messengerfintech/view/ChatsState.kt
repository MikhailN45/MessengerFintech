package com.study.messengerfintech.view

sealed class ChatsState {
    data object Success : ChatsState()

    data object Loading : ChatsState()

    class Error(val error: Throwable) : ChatsState()
}