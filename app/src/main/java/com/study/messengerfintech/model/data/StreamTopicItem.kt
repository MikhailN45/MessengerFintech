package com.study.messengerfintech.model.data

sealed interface StreamTopicItem
//todo remove variables
data class StreamItem(
    val streamId: Int,
    val title: String,
    var topics: List<TopicItem>,
    var isExpanded: Boolean = false,
    var isLoading: Boolean = false
) : StreamTopicItem

data class TopicItem(
    val topicId: Int,
    val streamId: Int,
    val title: String,
    var messageCount: Int
) : StreamTopicItem