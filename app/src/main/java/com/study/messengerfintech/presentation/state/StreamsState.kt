package com.study.messengerfintech.presentation.state

import com.study.messengerfintech.domain.model.StreamTopicItem

data class StreamsState(
    val items: List<StreamTopicItem>,
    val isLoading: Boolean = false
) : State