package com.study.messengerfintech.presentation.events

import com.study.messengerfintech.domain.model.Reaction

sealed interface ChatEvent {
    sealed interface LoadMessages : ChatEvent {
        data class Private(
            val userEmail: String
        ) : LoadMessages

        data class Topic(
            val streamId: Int,
            val topicName: String
        ) : LoadMessages
    }

    sealed interface SendMessage : ChatEvent {
        data class Private(
            val userEmail: String,
            val content: String
        ) : SendMessage

        data class Topic(
            val streamId: Int,
            val topicName: String,
            val content: String
        ) : SendMessage
    }

    sealed interface Emoji : ChatEvent {
        data class Add(
            val messageId: Int,
            val emojiName: String
        ) : Emoji

        data class Remove(
            val messageId: Int,
            val emojiName: String
        ) : Emoji
    }
}