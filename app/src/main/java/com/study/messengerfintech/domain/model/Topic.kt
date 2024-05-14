package com.study.messengerfintech.domain.model

data class Topic(
    val title: String,
    var messageCount: Int
)

fun toTopicItem(topicResponses: List<Topic>, streamId: Int): List<TopicItem> =
    topicResponses.mapIndexed { index, topic ->
        TopicItem(
            title = topic.title,
            messageCount = topic.messageCount,
            topicId = index,
            streamId = streamId
        )
    }
