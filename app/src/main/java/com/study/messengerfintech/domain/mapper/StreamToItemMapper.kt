package com.study.messengerfintech.domain.mapper

import com.study.messengerfintech.domain.model.Stream
import com.study.messengerfintech.model.data.StreamItem

class StreamToItemMapper : (Stream) -> (StreamItem) {
    private val topicToItemMapper: TopicToItemMapper = TopicToItemMapper()
    override fun invoke(stream: Stream): StreamItem {
        return StreamItem(
            streamId = stream.id,
            title = stream.title,
            isExpanded = false,
            topics = topicToItemMapper(stream.topics, stream.id)
        )
    }
}