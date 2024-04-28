package com.study.messengerfintech.presentation.state

import com.study.messengerfintech.domain.model.StreamTopicItem

sealed interface State {
    data object Success : State

    data object Loading : State

    data object Error : State

    data class Streams(val items: List<StreamTopicItem>) : State
}

