package com.study.messengerfintech.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Topic(
    val title: String,
    var messageCount: Int = 0,
    val streamId: Int = 0
)

fun List<Topic>.toTopicItems(streamId: Int): List<TopicItem> = mapIndexed { index, topic ->
    TopicItem(
        title = topic.title,
        messageCount = topic.messageCount,
        topicId = index,
        streamId = streamId
    )
}