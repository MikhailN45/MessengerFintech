package com.study.messengerfintech.presentation.state

import com.study.messengerfintech.domain.model.User

sealed interface UsersState : State {
    data class Success(val users: List<User>) : State

    data object Loading : State

    data class Error(val error: String) : State
}