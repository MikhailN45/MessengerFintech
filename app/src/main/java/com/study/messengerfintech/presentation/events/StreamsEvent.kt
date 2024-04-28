package com.study.messengerfintech.presentation.events

import com.study.messengerfintech.domain.model.StreamItem
import com.study.messengerfintech.domain.model.User

sealed interface StreamsEvent {
    data class SearchForStreams(
        val query: String = ""
    ) : StreamsEvent

    data class ExpandStream(
        val stream: StreamItem
    ): StreamsEvent

    sealed interface OpenChat : StreamsEvent {
        data class Private(
            val user: User
        ) : OpenChat

        data class Topic(
            val streamId: Int,
            val topic: String,
        ) : OpenChat
    }
}