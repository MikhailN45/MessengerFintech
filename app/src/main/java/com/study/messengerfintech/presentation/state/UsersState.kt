package com.study.messengerfintech.presentation.state

import com.study.messengerfintech.domain.model.User

data class UsersState(
    val users: List<User>,
    val isLoading: Boolean = false
) : State