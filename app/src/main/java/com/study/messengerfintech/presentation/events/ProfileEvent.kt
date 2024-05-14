package com.study.messengerfintech.presentation.events

import com.study.messengerfintech.domain.model.User

sealed interface ProfileEvent {
    data class SetUserStatus(
        val user: User
    ) : ProfileEvent
}