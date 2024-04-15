package com.study.messengerfintech.domain.usecase

import com.study.messengerfintech.model.data.ChatItem
import com.study.messengerfintech.model.data.StreamAndChatItem
import com.study.messengerfintech.model.data.StreamItem

interface CheckSubscribedUseCase : (List<StreamAndChatItem>) -> List<StreamAndChatItem> {
    override fun invoke(items: List<StreamAndChatItem>): List<StreamAndChatItem>
}

class CheckSubscribedUseCaseImpl : CheckSubscribedUseCase {
    override fun invoke(items: List<StreamAndChatItem>): List<StreamAndChatItem> {
        val subscribedItems = mutableListOf<StreamAndChatItem>()
        var streamItem: StreamItem? = null

        loop@ for (item in items)
            when (item) {
                is StreamItem -> {
                    item.isExpanded = false
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