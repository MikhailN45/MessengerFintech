package com.study.messengerfintech.presentation.state

import com.study.messengerfintech.domain.model.User

data class ProfileState(val user: User) : State