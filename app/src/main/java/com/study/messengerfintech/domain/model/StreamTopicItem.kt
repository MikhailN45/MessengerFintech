package com.study.messengerfintech.domain.model

sealed interface StreamTopicItem
data class StreamItem(
    val streamId: Int,
    val title: String,
    var topics: List<TopicItem>,
    var isExpanded: Boolean = false
) : StreamTopicItem

data class TopicItem(
    val topicId: Int,
    val streamId: Int,
    val title: String,
    var messageCount: Int
) : StreamTopicItem