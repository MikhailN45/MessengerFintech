package com.study.messengerfintech.domain.data
//todo change variables to values
sealed interface StreamTopicItem
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