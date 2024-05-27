package com.study.messengerfintech.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Topic(
    val title: String,
    val messageCount: Int,
    val streamId: Int
)

fun List<Topic>.toTopicItems(streamId: Int): List<TopicItem> = mapIndexed { index, topic ->
    TopicItem(
        title = topic.title,
        messageCount = topic.messageCount,
        topicId = index,
        streamId = streamId
    )
}