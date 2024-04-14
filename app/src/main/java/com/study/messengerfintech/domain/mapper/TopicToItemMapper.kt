package com.study.messengerfintech.domain.mapper

import com.study.messengerfintech.domain.model.Topic
import com.study.messengerfintech.model.data.TopicItem

class TopicToItemMapper : (List<Topic>, Int) -> (List<TopicItem>) {
    override fun invoke(topicResponses: List<Topic>, streamId: Int): List<TopicItem> {
        return topicResponses.mapIndexed { index, topic ->
            TopicItem(
                title = topic.title,
                messageCount = 0,
                topicId = index,
                streamId = streamId
            )
        }
    }
}