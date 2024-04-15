package com.study.messengerfintech.view.state

sealed interface ChatState {
    //todo add "message" to success constructor (from fragment)
    data object Success : ChatState
    data object Loading : ChatState
    class Error(val error: Throwable) : ChatState
}