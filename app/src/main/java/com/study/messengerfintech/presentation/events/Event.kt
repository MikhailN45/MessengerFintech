package com.study.messengerfintech.presentation.events

import com.study.messengerfintech.domain.model.StreamItem
import com.study.messengerfintech.domain.model.User

sealed interface Event {

    data class SearchForStreams(
        val query: String = ""
    ) : Event

    data class ExpandStream(
        val stream: StreamItem
    ): Event

    sealed interface OpenChat : Event {
        data class Private(
            val user: User
        ) : OpenChat

        data class Topic(
            val streamId: Int,
            val topic: String,
        ) : OpenChat
    }
}