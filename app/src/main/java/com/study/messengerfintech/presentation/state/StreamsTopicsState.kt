package com.study.messengerfintech.presentation.state

sealed interface StreamsTopicsState {
    data object Success : StreamsTopicsState
    data object Loading : StreamsTopicsState
    class Error(val error: Throwable) : StreamsTopicsState
}