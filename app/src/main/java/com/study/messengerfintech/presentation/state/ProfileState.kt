package com.study.messengerfintech.presentation.state

import com.study.messengerfintech.domain.model.User

sealed interface ProfileState : State {
    data class Success(val user: User) : State

    data object Loading : State

    data class Error(val error: String) : State
}