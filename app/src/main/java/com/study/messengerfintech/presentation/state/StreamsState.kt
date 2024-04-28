package com.study.messengerfintech.presentation.state

import com.study.messengerfintech.domain.model.StreamTopicItem

sealed interface StreamsState : State {
    data class Success(val items: List<StreamTopicItem>) : State

    data object Loading : State

    data class Error(val error: String) : State
}