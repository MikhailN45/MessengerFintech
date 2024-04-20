package com.study.messengerfintech.presentation.state

import com.study.messengerfintech.domain.model.Message

sealed interface ChatState {
    data class Success(val messages: List<Message>) : ChatState
    data object Loading : ChatState
    class Error(val error: Throwable) : ChatState
}