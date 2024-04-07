package com.study.messengerfintech.view.states

sealed class StreamsAndChatsState {
    data object Success : StreamsAndChatsState()

    data object Loading : StreamsAndChatsState()

    data object Error : StreamsAndChatsState()
}