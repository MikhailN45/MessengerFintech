package com.study.messengerfintech.domain.mapper

import com.study.messengerfintech.model.data.Stream
import com.study.messengerfintech.model.data.StreamItem

class StreamMapper : (Stream) -> (StreamItem) {
    private val chatMapper: ChatMapper = ChatMapper()
    override fun invoke(stream: Stream): StreamItem {
        return StreamItem(
            id = stream.id,
            title = stream.title,
            isSubscribed = stream.isSubscribed,
            isExpanded = false,
            chats = chatMapper(stream.chats, stream.id)
        )
    }
}
