package com.study.messengerfintech.presentation.state

import com.study.messengerfintech.domain.model.Message

data class ChatState(
    val title: String = "",
    val messages: List<Message> = listOf(),
    val loaded: Boolean = false,
    val isLoading: Boolean = false,
) : State