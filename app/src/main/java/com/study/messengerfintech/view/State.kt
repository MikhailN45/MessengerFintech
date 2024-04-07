package com.study.messengerfintech.view

sealed class State {
    data object Result : State()

    data object Loading : State()

    class Error(val error: Throwable) : State()
}