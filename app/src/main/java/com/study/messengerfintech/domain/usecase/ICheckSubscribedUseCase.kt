package com.study.messengerfintech.domain.usecase

import com.study.messengerfintech.model.data.ChatItem
import com.study.messengerfintech.model.data.StreamAndChatItem
import com.study.messengerfintech.model.data.StreamItem

interface ICheckSubscribedUseCase : (List<StreamAndChatItem>) -> List<StreamAndChatItem> {
    override fun invoke(items: List<StreamAndChatItem>): List<StreamAndChatItem>
}

class CheckSubscribedUseCase : ICheckSubscribedUseCase {
    override fun invoke(items: List<StreamAndChatItem>): List<StreamAndChatItem> {
        val subscribedItems = mutableListOf<StreamAndChatItem>()
        var streamItem: StreamItem? = null

        loop@ for (item in items)
            when (item) {
                is StreamItem -> {
                    if (item.isSubscribed) subscribedItems.add(item)
                    streamItem = item
                }

                is ChatItem -> {
                    if (streamItem?.isSubscribed == false) continue@loop
                    subscribedItems.add(item)
                }
            }

        return subscribedItems
    }
}