package com.study.messengerfintech.domain.mapper

import com.study.messengerfintech.model.data.Chat
import com.study.messengerfintech.model.data.ChatItem

class ChatMapper : (List<Chat>, Int) -> (List<ChatItem>) {
    override fun invoke(chats: List<Chat>, streamId: Int): List<ChatItem> {
        return chats.mapIndexed { index, chat ->
            ChatItem(
                title = chat.title,
                messageCount = chat.messages.size,
                chatId = index,
                streamId = streamId
            )
        }
    }
}