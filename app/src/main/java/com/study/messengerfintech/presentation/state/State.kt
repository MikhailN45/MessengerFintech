package com.study.messengerfintech.presentation.state

import com.study.messengerfintech.domain.model.Message
import com.study.messengerfintech.domain.model.StreamTopicItem
import com.study.messengerfintech.domain.model.User

sealed interface State {
    data object Success : State

    data object Loading : State

    data object Error : State

    /*data class Chat(
        val name: String = "",
        val messages: List<Message> = listOf(),
        val loaded: Boolean = false
    ) : State*/

    data class Users(val users: List<User>) : State

    data class Streams(val items: List<StreamTopicItem>) : State

    data class Profile(val user: User) : State
}

