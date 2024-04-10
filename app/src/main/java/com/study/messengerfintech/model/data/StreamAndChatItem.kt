package com.study.messengerfintech.model.data

sealed class StreamAndChatItem

data class StreamItem(
    val id: Int,
    val title: String,
    var isExpanded: Boolean = false
): StreamAndChatItem()

data class ChatItem(
    val title: String,
    val messageCount: Int,
    val streamId: Int,
    val chatId: Int
) : StreamAndChatItem()