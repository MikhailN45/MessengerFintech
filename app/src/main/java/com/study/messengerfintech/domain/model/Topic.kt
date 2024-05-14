package com.study.messengerfintech.domain.model

data class Topic(
    val title: String,
    var messageCount: Int
)

fun List<Topic>.toTopicItems(streamId: Int): List<TopicItem> = mapIndexed { index, topic ->
    TopicItem(
        title = topic.title,
        messageCount = topic.messageCount,
        topicId = index,
        streamId = streamId
    )
}
