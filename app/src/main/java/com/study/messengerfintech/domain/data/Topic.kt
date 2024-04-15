package com.study.messengerfintech.domain.data

data class Topic(
    val title: String,
    val lastMessageID: Int,
)

fun toTopicItem(topicResponses: List<Topic>, streamId: Int): List<TopicItem> =
    topicResponses.mapIndexed { index, topic ->
        TopicItem(
            title = topic.title,
            messageCount = 0,
            topicId = index,
            streamId = streamId
        )
    }
