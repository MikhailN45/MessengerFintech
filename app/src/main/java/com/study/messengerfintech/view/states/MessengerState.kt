package com.study.messengerfintech.view.states

sealed class MessengerState {
    data object Success : MessengerState()

    data object Loading : MessengerState()

    class Error(val error: Throwable) : MessengerState()
}