package com.study.messengerfintech.presentation.events

import com.study.messengerfintech.domain.model.User

sealed class Event {
    sealed class OpenChat : Event() {
        data class Private(
            val user: User
        ) : OpenChat()

        data class Topic(
            val streamId: Int,
            val topic: String,
        ) : OpenChat()
    }

    sealed class LoadMessages : Event() {
        data class Private(
            val userEmail: String
        ) : LoadMessages()

        data class Topic(
            val streamId: Int,
            val topicName: String
        ) : LoadMessages()
    }

    sealed class SendMessage : Event() {
        data class Private(
            val userEmail: String,
            val content: String
        ) : SendMessage()

        data class Topic(
            val streamId: Int,
            val topicName: String,
            val content: String
        ) : SendMessage()
    }

    sealed class Emoji : Event() {
        data class Add(
            val messageId: Int,
            val emojiName: String
        ) : Emoji()

        data class Remove(
            val messageId: Int,
            val emojiName: String
        ) : Emoji()
    }

    data class SearchForStreams(
        val query: String = ""
    ) : Event()

    data class SearchForUsers(
        val query: String = ""
    ) : Event()

    data class SetUserStatus(
        val user: User
    ) : Event()
}