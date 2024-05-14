package com.study.messengerfintech.presentation.events

sealed interface UsersEvent {
    data class SearchForUsers(
        val query: String = ""
    ) : UsersEvent
}