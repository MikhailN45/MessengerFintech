package com.study.messengerfintech.view.state

sealed interface StreamsTopicsState {
    data object Success : StreamsTopicsState
    data object Loading : StreamsTopicsState
    class Error(val error: Throwable) : StreamsTopicsState
}